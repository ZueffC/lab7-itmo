package itmo.lab5.shared;

import java.io.Serializable;

public enum CommandType implements Serializable {
    EXIT,
    HELP,
    INFO,
    SHOW,
    INSERT,
    UPDATE,
    REMOVE_KEY,
    CLEAR,
    HISTORY,
    PRINT_FIELD_ASCENDING_NUMBER_OF_ROOMS,
    FILTER_LESS_THAN_VIEW,
    FILTER_GREATER_THAN_VIEW,
    REPLACE_IF_LOWER,
    REPLACE_IF_GREATER,
    EXECUTE_SCRIPT,
    SERVER_SAVE,
    SIGN_UP,
    SIGN_IN,
}