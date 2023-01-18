
package accessDB;

import java.io.*;
public class UserInput
{
    static int getInteger(){
      String line;
      InputStreamReader eisodosString = new InputStreamReader (System.in);
      BufferedReader br = new BufferedReader (eisodosString);
      try{
         line = br.readLine();
         int i = Integer.parseInt (line);
         return i;
      }
      catch (IOException | NumberFormatException e){
         return -1;
     }
  }

    static float getFloat(){
      String line;
      InputStreamReader eisodosString = new InputStreamReader (System.in);
      BufferedReader br = new BufferedReader (eisodosString);
      try{
         line = br.readLine();
         float i = Float.parseFloat (line);
         return i;
      }
      catch (IOException | NumberFormatException e){
         return -1;
     }
  }

    static double getDouble(){
      String line;
      InputStreamReader eisodosString = new InputStreamReader (System.in);
      BufferedReader br = new BufferedReader (eisodosString);
      try{
         line = br.readLine();
         double i = Double.parseDouble (line);
         return i;
      }
      catch (IOException | NumberFormatException e){
         return -1;
     }
  }

    static short getShort(){
      String line;
      InputStreamReader eisodosString = new InputStreamReader (System.in);
      BufferedReader br = new BufferedReader (eisodosString);
      try{
         line = br.readLine();
         short i = Short.parseShort (line);
         return i;
      }
      catch (IOException | NumberFormatException e){
         return -1;
     }
  }

    static long getLong(){
      String line;
      InputStreamReader eisodosString = new InputStreamReader (System.in);
      BufferedReader br = new BufferedReader (eisodosString);
      try{
         line = br.readLine();
         long i = Long.parseLong (line);
         return i;
      }
      catch (IOException | NumberFormatException e){
         return -1;
     }
  }

    static byte getByte(){
      String line;
      InputStreamReader eisodosString = new InputStreamReader (System.in);
      BufferedReader br = new BufferedReader (eisodosString);
      try{
         line = br.readLine();
         byte i = Byte.parseByte(line);
         return i;
      }
      catch (IOException | NumberFormatException e){
         return -1;
      }
  }

    public static char getChar(){
      InputStreamReader eisodosString = new InputStreamReader (System.in);
      BufferedReader br = new BufferedReader (eisodosString);
      char c;
      try{
         c = (char)br.read();
         return c;
      }
      catch (IOException | NumberFormatException e){
         return 'L';	
      }
  }

    public static String getString(){
      String line;
      InputStreamReader eisodosString=new InputStreamReader (System.in);
      BufferedReader br=new BufferedReader(eisodosString);
      try{
         line=br.readLine();
         return line;
      }
      catch(IOException | NumberFormatException e){
         return "Lathos";
      }
  }
}	
