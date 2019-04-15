package io;

import protocol.Parameters;

public interface InputManager {
    Parameters read() throws Exception;
    void save(String path, Parameters parameters) throws Exception;
}
