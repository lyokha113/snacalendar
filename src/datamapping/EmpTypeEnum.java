package datamapping;

/**
 * Enum for list of employee type base on database
 *
 */
public enum EmpTypeEnum {
    FULLTIME(1), PARTIME(2), TEMPORARY(3);

    private int empTypeID;

    public int getEmpTypeID() {
        return empTypeID;
    }

    EmpTypeEnum(int empTypeID) {
        this.empTypeID = empTypeID;
    }
}
