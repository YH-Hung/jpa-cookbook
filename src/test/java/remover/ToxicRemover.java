package remover;

import eu.rekawek.toxiproxy.Proxy;
import lombok.SneakyThrows;

import java.util.TimerTask;
import java.util.function.Consumer;

public class ToxicRemover extends TimerTask {

    private final Proxy proxy;
    private final String removedToxicName;

    public ToxicRemover(Proxy proxy, String removedToxicName) {
        this.proxy = proxy;
        this.removedToxicName = removedToxicName;
    }

    @SneakyThrows
    @Override
    public void run() {
        proxy.toxics().get(removedToxicName).remove();
    }
}
