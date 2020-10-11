package edu.uwlax.himal.data.impl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DBFParsingUtil
{
    public static List<String> getHeaders(File file)
    {
        try
        {
            List<String> names = new ArrayList<>();

            FileInputStream stream = new FileInputStream(file);
            // Don't care about most of file header
            stream.readNBytes(32);

            byte[] fieldDescriptor;

            while ((fieldDescriptor = stream.readNBytes(32))[0] != 0x0D)
            {
                int i = 0;
                // Finds null-terminated string length
                //noinspection StatementWithEmptyBody
                while (++i < fieldDescriptor.length && fieldDescriptor[i] != 0x00) ;

                String name = new String(fieldDescriptor, 0, i);

                names.add(name);
            }

            stream.close();

            return names;
        } catch (FileNotFoundException ex)
        {
            throw new RuntimeException("Could not find the specified file", ex);
        } catch (IOException ex)
        {
            throw new RuntimeException("Could not load the specified file", ex);
        }
    }
}
