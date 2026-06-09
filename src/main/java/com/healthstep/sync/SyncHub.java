package com.healthstep.sync;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Component
public class SyncHub {
  private final Map<Long, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

  // Track per-emitter heartbeat tasks for clean cancellation
  private final ConcurrentMap<SseEmitter, ScheduledFuture<?>> heartbeats = new ConcurrentHashMap<>();
  private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, r -> {
    Thread t = new Thread(r, "sse-heartbeat");
    t.setDaemon(true);
    return t;
  });

  // 25s works well under mobile/proxy idle limits
  private static final long HEARTBEAT_MS = 25_000L;

  public SseEmitter subscribe(long userId) {
    SseEmitter emitter = new SseEmitter(0L); // no timeout, we keep it alive manually
    emitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);

    // Cleanup runnable shared by completion/timeout/error
    Runnable cleanup = () -> {
      CopyOnWriteArrayList<SseEmitter> list = emitters.getOrDefault(userId, new CopyOnWriteArrayList<>());
      list.remove(emitter);
      ScheduledFuture<?> hb = heartbeats.remove(emitter);
      if (hb != null) hb.cancel(true);
    };
    emitter.onCompletion(cleanup);
    emitter.onTimeout(cleanup);

    // Start heartbeat
    ScheduledFuture<?> hb = scheduler.scheduleAtFixedRate(
      () -> sendHeartbeat(emitter),
      HEARTBEAT_MS,
      HEARTBEAT_MS,
      TimeUnit.MILLISECONDS
    );
    heartbeats.put(emitter, hb);

    // Initial hello so clients know connection is live
    try {
      emitter.send(SseEmitter.event().name("hello").data("ok").reconnectTime(5000));
    } catch (IOException ignored) {}

    return emitter;
  }

  private void sendHeartbeat(SseEmitter e) {
    try {
      e.send(SseEmitter.event()
              .name("ping")
              .data(Instant.now().toString())
              .reconnectTime(5000));
    } catch (Exception ex) {
      try { e.complete(); } catch (Exception ignored) {}
      ScheduledFuture<?> hb = heartbeats.remove(e);
      if (hb != null) hb.cancel(true);
    }
  }

  public void publish(long userId, String type) {
    List<SseEmitter> list = emitters.getOrDefault(userId, new CopyOnWriteArrayList<>());
    for (SseEmitter e : new ArrayList<>(list)) {
      try {
        e.send(SseEmitter.event()
                .name("changed")
                .data(type)
                .reconnectTime(5000)); // e.g. "water", "sleep", "workout", "nutrition"
      } catch (Exception ex) {
        try { e.complete(); } catch (Exception ignored) {}
        list.remove(e);
        ScheduledFuture<?> hb = heartbeats.remove(e);
        if (hb != null) hb.cancel(true);
      }
    }
  }
}