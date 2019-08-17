package net.vpc.app.kifkif;

//package net.vpc.kifkif;
//
////import sun.net.www.http.HttpClient;
//
//import javax.swing.*;
//import java.awt.*;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.io.InputStream;
//import java.io.FileOutputStream;
//import java.net.Authenticator;
//import java.net.PasswordAuthentication;
//import java.net.URL;
//import java.net.URLConnection;
////import org.apache.commons.httpclient.*;
////import org.apache.commons.httpclient.auth.AuthScope;
////import org.apache.commons.httpclient.methods.GetMethod;
//import net.vpc.util.IOUtils;
//
///**
// * @author Taha Ben Salah (taha.bensalah@gmail.com)
// * @creationtime 4 oct. 2007 14:39:02
// */
//public class Toto {
////    public static void main(String[] args) {
//////        HTTPConnection c=new  HTTPConnection(new URL("http://dbclient.dev.java.net/release/dbclient.zip"));
////        HttpClient httpclient = new HttpClient();
////          httpclient.getHostConfiguration().setProxy("192.168.1.145", 80);
////          httpclient.getState().setProxyCredentials(AuthScope.ANY,
////          new UsernamePasswordCredentials("afafkaaf", "lttaf753"));
////          GetMethod httpget = new GetMethod("http://dbclient.dev.java.net/release/dbclient.zip");
////          try {
////            httpclient.executeMethod(httpget);
////            System.out.println(httpget.getStatusLine());
////            InputStream bodyAsStream = httpget.getResponseBodyAsStream();
////              ProgressMonitorInputStream pm=new ProgressMonitorInputStream(null,"Hey",bodyAsStream);
////              FileOutputStream fos=new FileOutputStream("/home/vpc/Desktop/rombatakaya.zip");
////              IOUtils.copy(pm,fos);
////              fos.close();
////              pm.close();
////          } catch(Exception e) {
////              e.printStackTrace();
////          } finally {
////            httpget.releaseConnection();
////          }
////    }
//
////    public static void main(String argv[]) throws Exception {
////        System.setProperty("http.proxyHost","192.168.1.145");
////        System.setProperty("http.proxyPort","80");
////        System.setProperty("https.proxyHost","192.168.1.145");
////        System.setProperty("https.proxyPort","80");
////        System.setProperty("proxyHost","192.168.1.145");
////        System.setProperty("proxyPort","80");
////        Authenticator.setDefault(new AuthImpl());
////        URL url = new URL("https://dbclient.dev.java.net/release/dbclient.zip");
//////        URL url = new URL("http://www.innovation.ch/java/HTTPClient/getting_started.html");
////        URLConnection connection = url.openConnection();
////        BufferedReader in = new BufferedReader(
////                new InputStreamReader(connection.getInputStream()));
////
////        String line;
////        StringBuffer sb = new StringBuffer();
////        while ((line = in.readLine()) != null) {
////            sb.append(line);
////        }
////        in.close();
////        System.out.println(sb.toString());
////        System.exit(0);
////    }
////
////    public static class AuthImpl extends Authenticator {
////        protected PasswordAuthentication getPasswordAuthentication() {
////            JTextField username = new JTextField();
////            JTextField password = new JPasswordField();
////            JPanel panel = new JPanel(new GridLayout(2, 2));
////            panel.add(new JLabel("User Name"));
////            panel.add(username);
////            panel.add(new JLabel("Password"));
////            panel.add(password);
////            int option = JOptionPane.showConfirmDialog(null, new Object[]{
////                    "Site: " + getRequestingHost(),
////                    "Realm: " + getRequestingPrompt(), panel},
////                    "Enter Network Password",
////                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
////            if (option == JOptionPane.OK_OPTION) {
////                String user = username.getText();
////                char pass[] = password.getText().toCharArray();
////                return new PasswordAuthentication(user, pass);
////            } else {
////                return null;
////            }
////        }
////    }
//    
//}
