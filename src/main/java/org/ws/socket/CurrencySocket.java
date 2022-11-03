package org.ws.socket;

import com.redislabs.redistimeseries.Aggregation;
import com.redislabs.redistimeseries.Value;
import io.quarkus.scheduler.Scheduled;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import org.ws.service.VolumeService;

@ServerEndpoint("/currency/socket/{iso}")
@ApplicationScoped
public class CurrencySocket {

    @Inject
    VolumeService service;

    Map<Session, String> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("iso") String iso) {
        sessions.put(session, iso);
    }

    @OnClose
    public void onClose(Session session, @PathParam("iso") String iso) {
        sessions.remove(session);
    }

    @OnError
    public void onError(Session session, @PathParam("iso") String iso, Throwable throwable) {
        sessions.remove(session);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("iso") String iso) {
        //Ignored
    }

    @Scheduled(every = "1s")
    void increment() {
        Instant time = Instant.now();
        sessions.forEach((session, iso) -> broadcast(session, iso, time));
    }


    private void broadcast(Session s, String iso, Instant time) {
        List<Value> values = service.findVolume(iso, time.plus(-1, ChronoUnit.DAYS), time.plus(1, ChronoUnit.DAYS),
            Aggregation.SUM);
        Value value = values.stream().findFirst().orElse(new Value(0, 0));
        String message = time.toString() + " : " + String.valueOf(value.getValue());
        s.getAsyncRemote().sendObject(message, result -> {
            if (result.getException() != null) {
                System.out.println("Unable to send message: " + result.getException());
            }
        });
    }

}