package plandy.javatradeclient;

import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;
import org.zeromq.ZMQ.Poller;

import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MarketDataService extends Thread {

    private final ArrayBlockingQueue<String> inBuffer;
    private final ArrayBlockingQueue<ZMsg> outBuffer;

    private final ZContext zContext;
    private final ZMQ.Socket zClientSocket;
    private final ZMQ.Poller zPoller;

    private final ConcurrentMap<String, IResultCallback> replyCallbackMap = new ConcurrentHashMap<>();

    private volatile boolean isRunning = false;

    public MarketDataService() {

        this.inBuffer = new ArrayBlockingQueue(100);
        this.outBuffer = new ArrayBlockingQueue(100);

        this.zContext =  new ZContext();
        this.zClientSocket = zContext.createSocket( ZMQ.DEALER );
        this.zClientSocket.connect( "tcp://localhost:5057" );

        zPoller = zContext.createPoller(1);
        zPoller.register( zClientSocket, Poller.POLLIN );

    }

    @Override
    public void run() {

        isRunning = true;

        while( isRunning ) {
            while( outBuffer.peek() != null ) {
                ZMsg outMessage = outBuffer.poll();
                System.out.println(outMessage + Calendar.getInstance().toInstant().toString());
                outMessage.send( zClientSocket );
            }

            int numEvents = zPoller.poll(2);

            for (int i = 0; i < numEvents; i++) {
                ZMsg zMsg = ZMsg.recvMsg( zClientSocket );
                System.out.println( zMsg.toString() + Calendar.getInstance().toInstant().toString() );

                ZFrame requestIDFrame = zMsg.poll();
                String requestID = new String( requestIDFrame.getData() );

                System.out.println( "Request ID : " + requestID + Calendar.getInstance().toInstant().toString() );

                ZFrame requestTypeFrame = zMsg.poll();
                String requestType = new String( requestTypeFrame.getData() );
                System.out.println( "Request type : " + requestType + Calendar.getInstance().toInstant().toString() );

                ZFrame dataFrame = zMsg.poll();
                String resultString = new String( dataFrame.getData() );
                System.out.println( "Result string : " + resultString + Calendar.getInstance().toInstant().toString() );

                IResultCallback resultCallback = replyCallbackMap.get( requestID );

                resultCallback.executeCallback( resultString );
            }

            try {
                Thread.sleep( 100 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void stopRunning() {
        isRunning = false;
    }

    public boolean requestData( IDataRequest p_dataRequest, IResultCallback p_callBack ) {
        System.out.println( "requesting data" + Calendar.getInstance().toInstant().toString() );

        String uuidString = generateRequestID();

        ZMsg requestMsg = p_dataRequest.toMsg();
        requestMsg.addFirst(uuidString);

        registerRequestCallback( uuidString, p_callBack );

        boolean isSuccess = outBuffer.offer( requestMsg );
        return isSuccess;
    }

    private void registerRequestCallback( String p_requestID, IResultCallback p_callBack ) {
        replyCallbackMap.put( p_requestID, p_callBack );
    }

    private static String generateRequestID() {
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();

        return uuidString;
    }

}
