package MailDownloader_MS;

import java.util.Date;
import java.util.InputMismatchException;

import com.google.gson.JsonParseException;
import com.microsoft.graph.http.GraphServiceException;
import com.microsoft.graph.models.extensions.User;
import java.util.Scanner;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;



public class App {
	@SuppressWarnings("unchecked")
	public static String getAccessTokenFromAuthService() {
		String accessToken = new String();
		
		JSONObject config = new JSONObject();
		JSONArray config_array = new JSONArray();
		
		
		File file_out =new File("config.json");
		final Properties oAuthProperties = new Properties();
        try {
            oAuthProperties.load(App.class.getClassLoader().getResourceAsStream("oAuth.properties"));
        } catch (IOException e) {
            System.out.println("Unable to read OAuth configuration. Make sure you have a properly formatted oAuth.properties file. See README for details.");
        }

        final String appId = oAuthProperties.getProperty("app.id");
        final String[] appScopes = oAuthProperties.getProperty("app.scopes").split(",");
        
     // Get an access token
        try {
	        if (file_out.createNewFile()) {
	          System.out.println("File created: " + file_out.getName());
	          Authentication.initialize(appId);
	          accessToken = Authentication.getUserAccessTokenFirst(appScopes);
	          config.put("output_path","");
	          config.put("fecha_inicial","");
	          config.put("fecha_final","");
	          config_array.add(config);
	          FileWriter myWriter = new FileWriter("config.json");
		      myWriter.write(config_array.toJSONString());
		      myWriter.flush();
		      myWriter.close();
	        } else {
	          System.out.println("File already exists.");
	          Authentication.initialize(appId);
	          accessToken = Authentication.getUserAccessTokenSilent(appScopes);
	        }
        }catch(IOException e) {
        	System.out.println("Fallo la autenticacion");
        }
		
		file_out = null;
		return accessToken;
	}
	
	public static void logOut() {
		File myObj = new File("config.json"); 
        if (myObj.delete()) { 
          System.out.println("Se elimino la sesion: " + myObj.getName());
          return;
        } else {
          System.out.println("Failed to delete the file.");
        } 
	}
	
	public static String getAccessTokenFromFile() throws org.json.simple.parser.ParseException {
		String accessToken = new String();
		
		JSONObject config = new JSONObject();
		JSONArray config_array = new JSONArray();
		JSONParser jsonParser = new JSONParser();
		
		
		try 
        {
			FileReader reader = new FileReader("config.json");
            //Read JSON file
            Object obj = jsonParser.parse(reader);
 
            config_array = (JSONArray) obj;
            System.out.println(config_array);
            
            

            config = (JSONObject) config_array.get(0);
            accessToken = (String) config.get("token");
 
        } catch (FileNotFoundException e) {
        	getAccessTokenFromAuthService();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
		
		return accessToken;
	}
	
	
	
	public static void main(String[] args) {
		
		 String accessToken = new String();
		 User user = new User();
		  System.out.println("Outlook Mail Attachment Downloader V0.7");
		  	try  
		  	{  
		  		accessToken = getAccessTokenFromAuthService();
		  	}  
		  	catch(Exception e)  
		  	{	  
		  		e.printStackTrace();  
		  	}  
		  	
	        System.out.println();
	     // Load OAuth settings
	        
	     // Greet the user
	        try {
	        	user = Graph.getUser(accessToken);
	        }catch(GraphServiceException e) {
	        	System.out.println("El token a expirado");
	        }
	        System.out.println("Welcome " + user.displayName);
	        System.out.println("Time zone: " + user.mailboxSettings.timeZone);
	        System.out.println();

	        Scanner input = new Scanner(System.in);
	        if(args[0] == "action") {
	        	Graph.getMessages(accessToken);
	        }
	        else {
		        int choice = -1;
	
		        while (choice != 0) {
		            System.out.println("Please choose one of the following options:");
		            System.out.println("0. Salir");
		            System.out.println("1. Fecha");
		            System.out.println("2. Logout");
		            System.out.println("3. Descargar Archivos Adjuntos");
	
		            try {
		                choice = input.nextInt();
		            } catch (InputMismatchException ex) {
		                // Skip over non-integer input
		            }
	
		            input.nextLine();
	
		            // Process user choice
		            switch(choice) {
		                case 0:
		                    // Exit the program
		                    System.out.println("Goodbye...");
		                    //System.exit(0);
		                    return;
		                    //break;
		                case 1:
		                    // Display access token
		                	//System.out.println("Access token: " + accessToken);
		                	String from = "2021/02/21";
		                	String cmp = "2021/02/22";
		                	String to = "2022/03/20";
	
		                	DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		                	
		                	try {
			                	Date date1 = format.parse(from);
			                	Date date2 = format.parse(cmp);
			                	Date date3 = format.parse(to);
			                	
			                	if(date1.before(date2) && date3.after(date2)) {
			                	   System.out.println("Date lies between from and to date");
			                	}
			                	else {
			                	   System.out.println("La fecha no esta entre from y to");
			                	}
		                	}catch(ParseException e){
		                		System.out.println("Parsing Error");
		                	}
		                    break;
		                case 2:
		                    // Logout
		                	File myObj = new File("config.txt"); 
		    	            if (myObj.delete()) { 
		    	              System.out.println("Deleted the file: " + myObj.getName());
		    	              return;
		    	            } else {
		    	              System.out.println("Failed to delete the file.");
		    	            } 
		                    break;
		                case 3:
		                    // Create a new event
		                	Graph.getMessages(accessToken);
		                    break;
		                default:
		                    System.out.println("Invalid choice");
		            }
		        }
	
		        input.close();
	        }
	}

}
