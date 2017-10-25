package plandy.javatradeclient;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;
import plandy.javatradeclient.controller.MainWindowController;
import org.zeromq.ZMQ.PollItem;
import org.zeromq.ZMQ.Poller;

import java.util.Calendar;
import java.util.concurrent.ArrayBlockingQueue;

public class MarketDataService extends Thread {

    private final ArrayBlockingQueue<String> inBuffer;
    private final ArrayBlockingQueue<String> outBuffer;

    private final ZContext zContext;
    private final ZMQ.Socket zClientSocket;

    private final ZMQ.Poller zPoller;

    private final PollItem[] items;

    private volatile boolean isRunning = false;

    public MarketDataService() {

        this.inBuffer = new ArrayBlockingQueue(100);
        this.outBuffer = new ArrayBlockingQueue(100);

        this.zContext =  new ZContext();
        this.zClientSocket = zContext.createSocket( ZMQ.DEALER );
        this.zClientSocket.connect( "tcp://localhost:5057" );

        items = new PollItem[] {
                new PollItem(zClientSocket, Poller.POLLIN)
        };

        zPoller = zContext.createPoller(1);
        zPoller.register( zClientSocket, Poller.POLLIN );

    }

    @Override
    public void run() {

        isRunning = true;

        while( isRunning ) {
            //System.out.println( "loop data service" + Calendar.getInstance().toInstant().toString() );
            while( outBuffer.peek() != null ) {
                String outMessage = outBuffer.poll();
                System.out.println(outMessage + Calendar.getInstance().toInstant().toString());
                //zClientSocket.send( outMessage );
                ZMsg.newStringMsg(outMessage).send(zClientSocket);
            }

            int numEvents = zPoller.poll(2);

            for (int i = 0; i < numEvents; i++) {
                ZMsg zMsg = ZMsg.recvMsg( zClientSocket );
                System.out.println( new String(zMsg.getFirst().getData()) + Calendar.getInstance().toInstant().toString() );
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

    public boolean requestData( MainWindowController.DataRequestTickers p_dataRequest ) {
        System.out.println( "requesting data" + Calendar.getInstance().toInstant().toString() );
        boolean isSuccess = outBuffer.offer( p_dataRequest.getRequestType() );
        return isSuccess;
    }


}
