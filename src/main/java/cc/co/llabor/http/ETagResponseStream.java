 
package cc.co.llabor.http;

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

public class ETagResponseStream extends ServletOutputStream
{

    public ETagResponseStream(OutputStream outputstream)
        throws IOException
    {
        closed = false;
        stream = null;
        stream = outputstream;
    }

    public void close()
        throws IOException
    {
        if(!closed)
        {
            stream.close();
            closed = true;
        }
    }

    public void flush()
        throws IOException
    {
        if(!closed)
            stream.flush();
    }

    public void write(int i)
        throws IOException
    {
        if(!closed)
            stream.write((byte)i);
    }

    public void write(byte abyte0[], int i, int j)
        throws IOException
    {
        if(!closed)
            stream.write(abyte0, i, j);
    }

    public void write(byte abyte0[])
        throws IOException
    {
        write(abyte0, 0, abyte0.length);
    }

    public boolean closed()
    {
        return closed;
    }

    public void reset()
    {
    }

    private boolean closed;
    private OutputStream stream;
	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setWriteListener(WriteListener arg0) {
		// TODO Auto-generated method stub
		
	}
}
