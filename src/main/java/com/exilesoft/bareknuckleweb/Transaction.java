package com.exilesoft.bareknuckleweb;

import java.io.Closeable;
import java.io.IOException;

public interface Transaction extends Closeable {

    void setCommit();

    @Override
    public void close() throws IOException;
}
