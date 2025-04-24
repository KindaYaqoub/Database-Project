package application;

public class Table {
	private int tableId;
	private int capacity;
	private int branchId;

	public Table(int tableId, int capacity, int branchId) {
		this.tableId = tableId;
		this.capacity = capacity;
		this.branchId = branchId;
	}

	public int getTableId() {
		return tableId;
	}

	public void setTableId(int tableId) {
		this.tableId = tableId;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getBranchId() {
		return branchId;
	}

	public void setBranchId(int branchId) {
		this.branchId = branchId;
	}

	@Override
	public String toString() {
		return "tableId=" + tableId + ",branchId=" +branchId;
	}

}