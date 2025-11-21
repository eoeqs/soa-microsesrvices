//package org.fergoeqs.exception;
//
//public class ResourceNotFoundException extends RuntimeException {
//    public ResourceNotFoundException(String message) {
//        super(message);
//    }
//
//    public ResourceNotFoundException(String resource, Long id) {
//        super(resource + " not found with id: " + id);
//    }
//}
package org.fergoeqs.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " not found with id: " + id);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}