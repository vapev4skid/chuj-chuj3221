package thunder.hack.features.cmd.impl;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;
import thunder.hack.features.cmd.Command;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class SendApiCommand extends Command {
    private final ExecutorService executor;
    private final ScheduledExecutorService scheduler;
    private final AtomicInteger activeRequests = new AtomicInteger(0);
    
    public SendApiCommand() {
        super("sendapi");
        // Create thread pool with configurable threads, keep alive for 10 seconds
        this.executor = new ThreadPoolExecutor(
            50, 100, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
        // Create scheduler for delayed tasks
        this.scheduler = Executors.newScheduledThreadPool(1);
    }
    
    @Override
    public void executeBuild(@NotNull LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(arg("count", IntegerArgumentType.integer(1, 1000)).executes(context -> {
            int count = context.getArgument("count", Integer.class);
            sendConcurrentRequests(count);
            return SINGLE_SUCCESS;
        }));
        
        builder.executes(context -> {
            sendMessage(".sendapi <number> - Send multiple concurrent API requests");
            sendMessage("Example: .sendapi 10 - Sends 10 requests simultaneously");
            return SINGLE_SUCCESS;
        });
    }
    
    private void sendConcurrentRequests(int count) {
        if (count <= 0) {
            sendMessage("Please specify a positive number of requests.");
            return;
        }
        
        sendMessage("Starting " + count + " concurrent API requests...");
        
        // Send all requests simultaneously
        for (int i = 0; i < count; i++) {
            final int requestNumber = i + 1;
            executor.submit(() -> {
                activeRequests.incrementAndGet();
                try {
                    // Send request to the telemetry endpoint
                    HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://plagai.org/apimimi/api?viev"))
                        .GET()
                        .build();
                    
                    HttpClient client = HttpClient.newHttpClient();
                    HttpResponse<String> response = client.send(request, 
                        HttpResponse.BodyHandlers.ofString());
                    
                    sendMessage("Request " + requestNumber + " completed with status: " + response.statusCode());
                    
                    // Keep connection alive for 10 seconds
                    Thread.sleep(10000);
                    
                } catch (Exception e) {
                    sendMessage("Request " + requestNumber + " failed: " + e.getMessage());
                } finally {
                    activeRequests.decrementAndGet();
                }
            });
        }
        
        // Schedule a status check after 5 seconds
        scheduler.schedule(() -> {
            sendMessage("Active requests remaining: " + activeRequests.get());
        }, 5, TimeUnit.SECONDS);
        
        // Schedule final status check after 10 seconds
        scheduler.schedule(() -> {
            sendMessage("All requests completed. Final active count: " + activeRequests.get());
        }, 10, TimeUnit.SECONDS);
    }
    
    public int getActiveRequestCount() {
        return activeRequests.get();
    }
}
