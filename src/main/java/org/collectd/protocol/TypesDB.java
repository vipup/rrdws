/*
 * jcollectd
 * Copyright (C) 2009 Hyperic, Inc.
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; only version 2 of the License is applicable.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA
 */

package org.collectd.protocol;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.collectd.mx.MBeanReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parser for collectd/src/types.db format.
 */
public class TypesDB {
    //List<DataSource> == plugin.h:data_set_t
    private Map<String,List<DataSource>> _types =
        new HashMap<String,List<DataSource>>();

	private static final Logger _log = LoggerFactory.getLogger(MBeanReceiver.class .getName());

    private static TypesDB _instance;

    public Map<String,List<DataSource>> getTypes() {
        return _types;
    }

    public List<DataSource> getType(String name) {
        return _types.get(name);
    }

    public static synchronized TypesDB getInstance() {
        if (_instance == null) {
            _instance = new TypesDB();
            try {
               _instance.load(); 
               _instance.load(Network.getProperty("typesdb"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return _instance;
    }

    public void load(String files) throws IOException {
        if (files == null) {
            return;
        }
        StringTokenizer tok = new StringTokenizer(files, File.pathSeparator);
        while (tok.hasMoreTokens()) {
            File file = new File(tok.nextToken());
            if (file.exists()) {
                load(file);
            }
        }
    }

    public void load() throws IOException {
        InputStream is =
            getClass().getClassLoader().getResourceAsStream("META-INF/types.db");
        try {
            load(is);
        } finally {
            is.close();
        }
    }

    //collectd/src/types_list.h:read_types_list
    public void load(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        try {
            load(is);
        } finally {
            is.close();
        }
    }

    public void load(InputStream is) throws IOException {
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(is));

        String line;

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.length() == 0) {
                continue;
            }
            if (line.charAt(0) == '#') {
                continue;
            }
            String[] fields = line.split("\\s+");
            String type = fields[0];

            for (int i=1; i<fields.length; i++) {
                String entry = fields[i];
                int len = entry.length();
                if (entry.charAt(len-1) == ',') {
                    entry = entry.substring(0, len-1);
                }

                String[] dsfields = entry.split(":");
                DataSource ds = new DataSource();
                ds._name = dsfields[0];

                String dstype = dsfields[1];
                if (dstype.equals(DataSource.GAUGE)) {
                    ds._type = DataSource.TYPE_GAUGE;
                }
                else {
                    ds._type = DataSource.TYPE_COUNTER;
                }

                ds._min = DataSource.toDouble(dsfields[2]);
                ds._max = DataSource.toDouble(dsfields[3]);

                List<DataSource> tds = _types.get(type);
                if (tds == null) {
                    tds = new ArrayList<DataSource>();
                    _types.put(type, tds);
                }
                tds.add(ds);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        TypesDB tl = new TypesDB();
        if (args.length == 0) {
            tl.load();
        }
        else {
            for (int i=0; i<args.length; i++) {
                tl.load(new File(args[i]));
            }
        }
        Map<String,List<DataSource>> types = tl.getTypes();
        for (Map.Entry<String, List<DataSource>> entry : types.entrySet()) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }
    }
}
