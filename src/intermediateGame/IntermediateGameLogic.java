/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intermediateGame;

import java.util.Random;

/**
 *
 * @author Dell
 */
public class IntermediateGameLogic {
   
    private char[][] board;
    private Random random = new Random();

    public IntermediateGameLogic() {
        resetGame();
    }

    public void resetGame() {
        board = new char[3][3];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                board[row][col] = '-';
            }
        }
    }

    public boolean makeMove(int row, int col, char player) {
        if (board[row][col] == '-') {
            board[row][col] = player;
            return true;
        }
        return false;
    }

    public char[][] getBoard() {
        return board;
    }

    public boolean checkWinner(char player) {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == player && board[i][1] == player && board[i][2] == player) {
                return true;
            }
        }
        for (int i = 0; i < 3; i++) {
            if (board[0][i] == player && board[1][i] == player && board[2][i] == player) {
                return true;
            }
        }
        if (board[0][0] == player && board[1][1] == player && board[2][2] == player) {
            return true;
        }
        if (board[0][2] == player && board[1][1] == player && board[2][0] == player) {
            return true;
        }
        return false;
    }

    public boolean isBoardFull() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[row][col] == '-') {
                    return false;
                }
            }
        }
        return true;
    }

    public int[] findBestMove(char player) {
        
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    board[i][j] = player;
                    if (checkWinner(player)) {
                        board[i][j] = '-'; 
                        return new int[]{i, j};
                    }
                    board[i][j] = '-'; 
                }
            }
        }

        
        char opponent = (player == 'X') ? 'O' : 'X';
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    board[i][j] = opponent;
                    if (checkWinner(opponent)) {
                        board[i][j] = '-';
                        return new int[]{i, j}; 
                    }
                    board[i][j] = '-'; 
                }
            }
        }

        
        if (random.nextDouble() < 0.4) { 
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j] == '-') {
                        return new int[]{i, j};
                    }
                }
            }
        }

       
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    return new int[]{i, j};
                }
            }
        }

        return null; 
    }
}