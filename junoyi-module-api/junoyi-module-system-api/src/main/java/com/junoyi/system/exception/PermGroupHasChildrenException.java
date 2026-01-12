package com.junoyi.system.exception;

/**
 * 权限组包含子权限组异常
 *
 * @author Fan
 */
public class PermGroupHasChildrenException extends PermGroupException {

    private static final long serialVersionUID = 1L;

    public PermGroupHasChildrenException(String message) {
        super(501, message, "HAS_CHILDREN");
    }
}
