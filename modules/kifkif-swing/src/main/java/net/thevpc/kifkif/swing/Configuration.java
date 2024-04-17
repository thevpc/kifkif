/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.kifkif.swing;

import net.thevpc.nuts.io.NPath;

import java.io.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author vpc
 */
public class Configuration {

    private Map<String, String> properties = new HashMap<String, String>();
    private NPath file;
    private boolean autoSave;

    public Configuration(NPath file, boolean autoSave) {
        this.file = file;
        this.autoSave = autoSave;
        load();
    }

    public boolean load() {
        Properties p = null;
        if (file.exists()) {
            try (InputStream in = file.getInputStream()) {
                p = new Properties();
                p.loadFromXML(in);
                properties.clear();
                p.putAll(p);
                return true;
            } catch (IOException exc) {
                //
            }
        }
        return false;
    }

    public boolean save() {
        Properties p = null;
        try {
            NPath parent = file.getParent();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try (OutputStream out = file.getOutputStream()) {
                p = new Properties();
                p.putAll(properties);
                p.storeToXML(out, "Configuration File");
            }
            return false;
        } catch (IOException exc) {
            //
        }
        return false;
    }

    public String getString(String name) {
        return properties.get(name);
    }

    public boolean getBoolean(String name, boolean defaultValue) {
        String s = getString(name);
        return s == null ? defaultValue : Boolean.valueOf(s);
    }

    public void setString(String key, String value) {
        properties.put(key, value);
        if (autoSave) {
            save();
        }
    }

    public void setBoolean(String key, boolean value) {
        setString(key, Boolean.toString(value));
    }

    public static Locale getLocaleFromString(String locale) {
        if (locale == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(locale, "_ ,;:/");
        int tcount = st.countTokens();
        String a1;
        String a2 = "";
        String a3 = "";
        if (tcount < 1 || tcount > 3) {
            throw new RuntimeException("bad locale : " + locale);
        }
        if (st.hasMoreTokens()) {
            a1 = st.nextToken();
            if (st.hasMoreTokens()) {
                a2 = st.nextToken();
                if (st.hasMoreTokens()) {
                    a2 = st.nextToken();
                    if (st.hasMoreTokens()) {
                        throw new RuntimeException("bad locale : " + locale);
                    }
                }
            }
        } else {
            throw new RuntimeException("bad locale : " + locale);
        }
        return new Locale(a1, a2, a3);
    }
}
