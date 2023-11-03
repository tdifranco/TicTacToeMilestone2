package clarkson.ee408.tictactoev4;

import static android.provider.SyncStateContract.Helpers.update;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import client.AppExecutors;
import client.SocketClient;
import socket.GamingResponse;
import socket.Request;
import socket.Response;


public class MainActivity extends AppCompatActivity {
    private TicTacToe tttGame;
    private Button [][] buttons;
    private TextView status;
    private Gson gson;
    private Handler handler = new Handler();
    private Boolean shouldRequestMove = false;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        tttGame = new TicTacToe(1);
        buildGuiByCode( );
        gson = new Gson();
        handler.postDelayed(runnableCode, 2000);
        updateTurnStatus();
    }
    // Define the code block to be executed
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            if (shouldRequestMove){
            Log.d("Handlers", "Called on main thread");
            requestMove();}
        }
    };
    private void requestMove(){
        Request request = new Request(Request.RequestType.REQUEST_MOVE, null);
        AppExecutors.getInstance().networkIO().execute(() -> {
            SocketClient socketClient = SocketClient.getInstance();
            GamingResponse response = socketClient.sendRequest(request, GamingResponse.class);
            if(response != null && response.getStatus() == Response.ResponseStatus.SUCCESS){
                if(response.getMove() != -1){
                    int row = response.getMove() / 3;
                    int col = response.getMove() % 3;
                    AppExecutors.getInstance().mainThread().execute(() ->
                            update(row, col));
                }
            }else {
                Toast.makeText(this, "ygf", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void sendMove(int row, int col) {
        Request request_ = new Request(Request.RequestType.SEND_MOVE, null);
        int move_num = row + col;
        String moveJson = gson.toJson(move_num);
        request_.setData(moveJson);
        SocketClient socketClient = SocketClient.getInstance();
        Response response = socketClient.sendRequest(move_num, Response.class);

    }

    private void updateTurnStatus() {
        shouldRequestMove = (tttGame.getPlayer() == tttGame.getTurn());
        if (shouldRequestMove) {
            status.setText("Your Turn");
        } else {
            status.setText("Waiting for Opponent");
            shouldRequestMove = true;
            enableButtons(shouldRequestMove);
        }

    }

    public void buildGuiByCode( ) {
        // Get width of the screen
        Point size = new Point( );
        getWindowManager( ).getDefaultDisplay( ).getSize( size );
        int w = size.x / TicTacToe.SIDE;

        // Create the layout manager as a GridLayout
        GridLayout gridLayout = new GridLayout( this );
        gridLayout.setColumnCount( TicTacToe.SIDE );
        gridLayout.setRowCount( TicTacToe.SIDE + 2 );

        // Create the buttons and add them to gridLayout
        buttons = new Button[TicTacToe.SIDE][TicTacToe.SIDE];
        ButtonHandler bh = new ButtonHandler( );

//        GridLayout.LayoutParams bParams = new GridLayout.LayoutParams();
//        bParams.width = w - 10;
//        bParams.height = w -10;
//        bParams.bottomMargin = 15;
//        bParams.rightMargin = 15;

        gridLayout.setUseDefaultMargins(true);

        for( int row = 0; row < TicTacToe.SIDE; row++ ) {
            for( int col = 0; col < TicTacToe.SIDE; col++ ) {
                buttons[row][col] = new Button( this );
                buttons[row][col].setTextSize( ( int ) ( w * .2 ) );
                buttons[row][col].setOnClickListener( bh );
                GridLayout.LayoutParams bParams = new GridLayout.LayoutParams();
//                bParams.width = w - 10;
//                bParams.height = w -40;

                bParams.topMargin = 0;
                bParams.bottomMargin = 10;
                bParams.leftMargin = 0;
                bParams.rightMargin = 10;
                bParams.width=w-10;
                bParams.height=w-10;
                buttons[row][col].setLayoutParams(bParams);
                gridLayout.addView( buttons[row][col]);
//                gridLayout.addView( buttons[row][col], bParams );
            }
        }

        // set up layout parameters of 4th row of gridLayout
        status = new TextView( this );
        GridLayout.Spec rowSpec = GridLayout.spec( TicTacToe.SIDE, 2 );
        GridLayout.Spec columnSpec = GridLayout.spec( 0, TicTacToe.SIDE );
        GridLayout.LayoutParams lpStatus
                = new GridLayout.LayoutParams( rowSpec, columnSpec );
        status.setLayoutParams( lpStatus );

        // set up status' characteristics
        status.setWidth( TicTacToe.SIDE * w );
        status.setHeight( w );
        status.setGravity( Gravity.CENTER );
        status.setBackgroundColor( Color.GREEN );
        status.setTextSize( ( int ) ( w * .15 ) );
        status.setText( tttGame.result( ) );

        gridLayout.addView( status );

        // Set gridLayout as the View of this Activity
        setContentView( gridLayout );
    }

    public void update( int row, int col ) {
        int play = tttGame.play( row, col );
        if( play == 1 )
            buttons[row][col].setText( "X" );
        else if( play == 2 )
            buttons[row][col].setText( "O" );
        if( tttGame.isGameOver( ) ) {
            status.setBackgroundColor( Color.RED );
            enableButtons( false );
            status.setText( tttGame.result( ) );
            showNewGameDialog( );	// offer to play again
        }
    }

    public void enableButtons( boolean enabled ) {
        for( int row = 0; row < TicTacToe.SIDE; row++ )
            for( int col = 0; col < TicTacToe.SIDE; col++ )
                buttons[row][col].setEnabled( enabled );
    }

    public void resetButtons( ) {
        for( int row = 0; row < TicTacToe.SIDE; row++ )
            for( int col = 0; col < TicTacToe.SIDE; col++ )
                buttons[row][col].setText( "" );
    }

    public void showNewGameDialog( ) {
        AlertDialog.Builder alert = new AlertDialog.Builder( this );
        alert.setTitle(tttGame.result());
        alert.setMessage( "Do you want to play again?" );
        PlayDialog playAgain = new PlayDialog( );
        alert.setPositiveButton( "YES", playAgain );
        alert.setNegativeButton( "NO", playAgain );
        alert.show( );
    }

    private class ButtonHandler implements View.OnClickListener {
        public void onClick( View v ) {
            Log.d("button clicked", "button clicked");
            for( int row = 0; row < TicTacToe.SIDE; row ++ )
                for( int column = 0; column < TicTacToe.SIDE; column++ )
                    if( v == buttons[row][column] )
                        if (buttons[row][column].isEnabled()) {
                            sendMove(row, column);
                            update(row, column);
        }
    }}
       private class PlayDialog implements DialogInterface.OnClickListener {
            public void onClick( DialogInterface dialog, int id ) {
                if( id == -1 ) /* YES button */ {
                    tttGame.resetGame( );
                    enableButtons( true );
                    resetButtons( );
                    status.setBackgroundColor( Color.GREEN );
                    status.setText( tttGame.result( ) );
                }
                else if( id == -2 ) // NO button
                    MainActivity.this.finish( );
            }
    }
}