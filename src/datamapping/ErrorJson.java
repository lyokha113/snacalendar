package datamapping;

import java.io.Serializable;

/**
 * Mapping json object for return error to client
 *
 */
public class ErrorJson implements Serializable {
    private boolean error;
    private String status;

    public ErrorJson(boolean error, String status) {
        this.error = error;
        this.status = status;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
