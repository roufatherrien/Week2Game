package codeguru.connect4;

import com.badlogic.gdx.InputAdapter;

import codeguru.connect4.Board.State;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;

public class Connect4 implements ApplicationListener {
    public enum PlayerType {
        HUMAN, COMPUTER
    }

    private enum GameState {
        START, PLAYING, FINISHED
    }

    private static float RADIUS = 1.0f;
    private static int SEGS = 100;

    private PlayerType[] types = null;
    private ShapeRenderer renderer = null;
    private Board board = null;
    private HumanPlayer[] players = new HumanPlayer[2];
    private int currPlayer = 0;
    private int diameter = 0;
    private int xMargin = 0;
    private GameState state = GameState.START;

    /**
     * Create a new Connect 4 game object with players of the given types.
     * 
     * @param types
     *            An array with two elements indicating whether to play with
     *            human or computer players.
     */
    public Connect4(PlayerType[] types) {
        this.types = types;
    }

    public void create() {
        if (this.renderer == null) {
            this.renderer = new ShapeRenderer();
        }

        if (this.board == null) {
            this.board = new Board();
        }

        for (int i = 0; i < 2; ++i) {
            switch (this.types[i]) {
            case HUMAN:
                this.players[i] = new HumanPlayer(this);
                break;
                
            case COMPUTER:
                break;
            }
        }

        this.currPlayer = 0;
        this.state = GameState.START;
    }

    public void render() {
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        switch (this.state) {
        case START:
            Gdx.input.setInputProcessor(new InputAdapter() {
                @Override
                public boolean touchUp(int x, int y, int pointer, int button) {
                    state = GameState.PLAYING;
                    return true;
                }
            });
            break;

        case PLAYING:
            // Make a move
            Gdx.input.setInputProcessor(this.players[currPlayer]);
            int move = this.players[this.currPlayer].move(this.board);

            if (move != -1) {
                System.out.println("move=" + move);

                this.board.move(move);
                State player = (currPlayer == 0) ? Board.State.PLAYER1
                        : Board.State.PLAYER2;

                System.out.println("player=" + player);

                if (this.board.isWin(player)) {
                    this.state = GameState.FINISHED;
                } else {
                    this.currPlayer = (this.currPlayer + 1) % 2;
                }
            }
            // Fall through to draw the board

        case FINISHED:
            // Draw the board
            renderer.begin(ShapeType.Filled);
            for (int row = 0; row < Board.ROW_COUNT; ++row) {
                for (int col = 0; col < Board.COL_COUNT; ++col) {
                    switch (this.board.getState(row, col)) {
                    case EMPTY:
                        renderer.setColor(Color.WHITE);
                        break;
                    case PLAYER1:
                        renderer.setColor(Color.RED);
                        break;
                    case PLAYER2:
                        renderer.setColor(Color.BLACK);
                        break;
                    }

                    float x = 2 * col + 1;
                    float y = 2 * row + 1;
                    renderer.circle(x, y, RADIUS, SEGS);
                }
            }
            renderer.end();
            break;
        }

    }

    public void resize(int width, int height) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);

        this.diameter = (int) Math.min((float) width / (float) Board.COL_COUNT,
                (float) height / (float) Board.ROW_COUNT);
        this.xMargin = ((width - this.diameter * Board.COL_COUNT) / 2);
        int yMargin = ((height - this.diameter * Board.ROW_COUNT) / 2);
        Gdx.gl.glViewport(this.xMargin, yMargin, width - 2 * this.xMargin,
                height - 2 * yMargin);

        Matrix4 ortho = new Matrix4().setToOrtho2D(0.0f, 0.0f,
                Board.COL_COUNT * 2.0f, Board.ROW_COUNT * 2.0f);
        this.renderer.setProjectionMatrix(ortho);
    }

    public void pause() {
    }

    public void resume() {
    }

    public void dispose() {
    }

    public int getGridSize() {
        return this.diameter;
    }

    public int getXMargin() {
        return this.xMargin;
    }
}