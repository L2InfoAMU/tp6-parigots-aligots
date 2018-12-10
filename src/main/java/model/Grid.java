package model;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


/**
 * {@link Grid} instances represent the grid in <i>The Game of Life</i>.
 */
public class Grid implements Iterable<Cell> {

    private final int numberOfRows;
    private final int numberOfColumns;
    private final Cell[][] cells;

    /**
     * Creates a new {@code Grid} instance given the number of rows and columns.
     *
     * @param numberOfRows    the number of rows
     * @param numberOfColumns the number of columns
     * @throws IllegalArgumentException if {@code numberOfRows} or {@code numberOfColumns} are
     *                                  less than or equal to 0
     */
    public Grid(int numberOfRows, int numberOfColumns) {
        this.numberOfRows = numberOfRows;
        this.numberOfColumns = numberOfColumns;
        this.cells = createCells();
    }

    /**
     * Returns an iterator over the cells in this {@code Grid}.
     *
     * @return an iterator over the cells in this {@code Grid}
     */

    @Override
    public Iterator<Cell> iterator() {
        return new GridIterator(this);
    }

    private Cell[][] createCells() {
        Cell[][] cells = new Cell[getNumberOfRows()][getNumberOfColumns()];
        for (int rowIndex = 0; rowIndex < getNumberOfRows(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < getNumberOfColumns(); columnIndex++) {
                cells[rowIndex][columnIndex] = new Cell();
            }
        }
        return cells;
    }

    /**
     * Returns the {@link Cell} at the given index.
     *
     * <p>Note that the index is wrapped around so that a {@link Cell} is always returned.
     *
     * @param rowIndex    the row index of the {@link Cell}
     * @param columnIndex the column index of the {@link Cell}
     * @return the {@link Cell} at the given row and column index
     */
    public Cell getCell(int rowIndex, int columnIndex) {
        return cells[getWrappedRowIndex(rowIndex)][getWrappedColumnIndex(columnIndex)];
    }

    private int getWrappedRowIndex(int rowIndex) {
        return (rowIndex + getNumberOfRows()) % getNumberOfRows();
    }

    private int getWrappedColumnIndex(int columnIndex) {
        return (columnIndex + getNumberOfColumns()) % getNumberOfColumns();
    }

    /**
     * Returns the number of rows in this {@code Grid}.
     *
     * @return the number of rows in this {@code Grid}
     */
    public int getNumberOfRows() {
        return numberOfRows;
    }

    /**
     * Returns the number of columns in this {@code Grid}.
     *
     * @return the number of columns in this {@code Grid}
     */
    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    private List<Cell> getNeighbours(int rowIndex, int columnIndex) {
        List<Cell> neighbours = new ArrayList();
        for(int i=-1;i<2;i++){
            for(int j=-1;j<2;j++){
                if(!(j==0 && i==0)) neighbours.add(getCell(rowIndex+i,columnIndex+j));
            }
        }
        return neighbours;
    }

    private int countAliveNeighbours(int rowIndex, int columnIndex) {
        List<Cell> neighbours = new ArrayList<Cell>(getNeighbours(rowIndex,columnIndex));
        int count=0;
        for (Cell cell: neighbours) {
            if(cell.isAlive()){
                count++;
            }
        }
        return count;
    }
    private CellState majorityNeighbours(List<Cell> neighbours){
        int redCount=0;
        for (Cell cell: neighbours) {
            if(cell.isAlive()){
                if(cell.getState().color==Color.RED){
                    redCount++;
                }
            }
        }
        if(redCount>1){
            return CellState.RED;
        }
        return CellState.BLUE;
    }

    private CellState calculateNextState(int rowIndex, int columnIndex) {
        if(countAliveNeighbours(rowIndex,columnIndex)==3){
            if(cells[rowIndex][columnIndex].isAlive()) {
                if (cells[rowIndex][columnIndex].getState().color == Color.RED) {
                    return CellState.RED;
                }
                else return CellState.BLUE;
            }
            else return majorityNeighbours(getNeighbours(rowIndex,columnIndex));
        }
        else if(cells[rowIndex][columnIndex].isAlive() && countAliveNeighbours(rowIndex,columnIndex)==2){
            if(cells[rowIndex][columnIndex].getState().color== Color.RED){
                return CellState.RED;
            }
            return CellState.BLUE;

        }
        else return CellState.DEAD;
    }


    private CellState[][] calculateNextStates() {
        CellState[][] nextCellState = new CellState[getNumberOfRows()][getNumberOfColumns()];
        for(int row=0;row<getNumberOfRows();row++){
            for(int col=0;col<getNumberOfColumns();col++){
                nextCellState[row][col]=calculateNextState(row,col);
            }
        }
        return nextCellState;
    }

    private void updateStates(CellState[][] nextState) {
        for(int i=0;i<nextState.length;i++) {
            for (int j = 0; j < nextState[0].length; j++) {
                this.cells[i][j].setState(nextState[i][j]);
            }
        }
    }

    /**
     * Transitions all {@link Cell}s in this {@code Grid} to the next generation.
     *
     * <p>The following rules are applied:
     * <ul>
     * <li>Any live {@link Cell} with fewer than two live neighbours dies, i.e. underpopulation.</li>
     * <li>Any live {@link Cell} with two or three live neighbours lives on to the next
     * generation.</li>
     * <li>Any live {@link Cell} with more than three live neighbours dies, i.e. overpopulation.</li>
     * <li>Any dead {@link Cell} with exactly three live neighbours becomes a live cell, i.e.
     * reproduction.</li>
     * </ul>
     */

    void updateToNextGeneration() {
        updateStates(calculateNextStates());
    }

    /**
     * Sets all {@link Cell}s in this {@code Grid} as dead.
     */

    void clear() {
        for (int row=0;row<getNumberOfRows();row++){
            for(int col=0;col<getNumberOfColumns();col++){
                this.cells[row][col].setState(CellState.DEAD);
            }
        }
    }

    /**
     * Goes through each {@link Cell} in this {@code Grid} and randomly sets its state as ALIVE or DEAD.
     *
     * @param random {@link Random} instance used to decide if each {@link Cell} is ALIVE or DEAD.
     * @throws NullPointerException if {@code random} is {@code null}.
     */

    void randomGeneration(Random random) {
        for(int row=0;row<getNumberOfRows();row++)
        {
            for (int col = 0; col < getNumberOfColumns(); col++)
            {
                if(random.nextBoolean()){
                    if(random.nextBoolean()){ this.cells[row][col].setState(CellState.RED); }
                    else {this.cells[row][col].setState(CellState.BLUE);}
                }
                else {this.cells[row][col].setState(CellState.DEAD);}
            }
        }

    }
}