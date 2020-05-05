package Projector;

import com.google.protobuf.Empty;
import com.google.protobuf.InvalidProtocolBufferException;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import name.benjaminabbitt.evented.bookmarks.Bookmarks;
import name.benjaminabbitt.evented.business.BusinessLogicGrpc;
import name.benjaminabbitt.evented.core.Evented;
import name.benjaminabbitt.evented.projector.ProjectorGrpc;
import name.benjaminabbitt.evented.projector.ProjectorGrpc.ProjectorImplBase;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class BookmarkProjector {
    private static final Logger logger = Logger.getLogger(Projector.BookmarkProjector.class.getName());
    private final Server server;

    public BookmarkProjector(int port) {
        this(ServerBuilder.forPort(port));
    }

    public BookmarkProjector(ServerBuilder<?> serverBuilder) {
        this.server = serverBuilder.addService(new BookmarkService()).build();
    }

    public void start() throws IOException {
        this.server.start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    Projector.BookmarkProjector.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    /**
     * Stop serving requests and shutdown resources.
     */
    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    private static class BookmarkService extends ProjectorImplBase {

        public BookmarkService() {
        }


        /**
         * @param request
         * @param responseObserver
         */
        @Override
        public void handleSync(Evented.EventBook request, StreamObserver<Evented.Projection> responseObserver) {
            super.handleSync(request, responseObserver);
        }

        /**
         * @param request
         * @param responseObserver
         */
        @Override
        public void handle(Evented.EventBook request, StreamObserver<Empty> responseObserver) {
            super.handle(request, responseObserver);
        }
    }

    public static void main(String[] args) throws Exception {
        Projector.BookmarkProjector server = new Projector.BookmarkProjector(8080);
        server.start();
        server.blockUntilShutdown();
    }
}