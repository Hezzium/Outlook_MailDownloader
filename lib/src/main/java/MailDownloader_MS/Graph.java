package MailDownloader_MS;

import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

//import java.util.Arrays;
//import java.util.Set;
import java.util.Base64;
import java.util.Date;

//import static org.junit.Assert.assertEquals;

//import org.json.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonParseException;
//import com.microsoft.graph.auth.confidentialClient.AuthorizationCodeProvider;
//import com.microsoft.graph.auth.confidentialClient.ClientCredentialProvider;
//import com.microsoft.graph.auth.enums.NationalCloud;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonElement;
import com.microsoft.graph.logger.DefaultLogger;
import com.microsoft.graph.logger.LoggerLevel;
/*import com.microsoft.graph.models.extensions.Attendee;
import com.microsoft.graph.models.extensions.DateTimeTimeZone;
import com.microsoft.graph.models.extensions.EmailAddress;*/
//import com.microsoft.graph.models.extensions.Event;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
//import com.microsoft.graph.models.extensions.ItemBody;
import com.microsoft.graph.models.extensions.User;
import com.microsoft.graph.models.extensions.Attachment;
import com.microsoft.graph.models.extensions.FileAttachment;
import com.microsoft.graph.models.extensions.Message;
//import com.microsoft.graph.models.generated.AttendeeType;
//import com.microsoft.graph.models.generated.BodyType;
//import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.extensions.GraphServiceClient;
//import com.microsoft.graph.requests.extensions.IEventCollectionPage;
//import com.microsoft.graph.requests.extensions.IEventCollectionRequestBuilder;
import com.microsoft.graph.requests.extensions.IMessageCollectionPage;
import com.microsoft.graph.requests.extensions.FileAttachmentRequestBuilder;
//import com.microsoft.graph.requests.extensions.FileAttachmentRequest;
import com.microsoft.graph.requests.extensions.IMessageCollectionRequestBuilder;
import com.microsoft.graph.requests.extensions.IAttachmentCollectionPage;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
//import com.microsoft.graph.requests.extensions.AttachmentCollectionPage;
//import com.microsoft.graph.serializer.AdditionalDataManager;
//import com.microsoft.graph.requests.extensions.IAttachmentCollectionRequestBuilder;
//import com.microsoft.graph.requests.extensions.AttachmentRequestBuilder;


public class Graph {
	
	private static IGraphServiceClient graphClient = null;
    private static SimpleAuthProvider authProvider = null;

    private static void ensureGraphClient(String accessToken) {
        if (graphClient == null) {
            // Create the auth provider
            authProvider = new SimpleAuthProvider(accessToken);
        	
//        	authProvider = new ClientCredentialProvider("f5dec953-d7fb-4979-a999-63576e590240", 
//        			Arrays.asList("https://graph.microsoft.com/.default"), 
//        			"Rl0g.7M_DhEZ.Cyf4M_fcf0Z3w5P50Q2-_", 
//        			"b75de1bf-7314-4e31-91e6-595fb37cc8b1", 
//        			NationalCloud.Global);

            // Create default logger to only log errors
            DefaultLogger logger = new DefaultLogger();
            logger.setLoggingLevel(LoggerLevel.ERROR);

            // Build a Graph client
            graphClient = GraphServiceClient.builder()
                .authenticationProvider(authProvider)
                .logger(logger)
                .buildClient();
        }
    }

    public static User getUser(String accessToken) {
        ensureGraphClient(accessToken);

        // GET /me to get authenticated user
        User me = graphClient
            .me()
            .buildRequest()
            .select("displayName,mailboxSettings")
            .get();

        return me;
    }
    
    public static void getMessages(String accessToken) {
    	    ensureGraphClient(accessToken);
    	    System.out.println("Obteniendo mensajes...");

    	    List<Option> options = new LinkedList<Option>();

    	    // Sort results by start time
   
			options.add(new QueryOption("$search", "\"hasAttachments:true\""));
			

    	    // Start and end times adjusted to user's time zone
    	  

    	    // GET /me/events
    	    IMessageCollectionPage messages = graphClient.me().messages()
    	    		.buildRequest(options)
    	    		.select("receivedDateTime,attachments")
    	    		.top(100)
    	    		.get();

    	    List<Message> allMessages = new LinkedList<Message>();

    	    // Create a separate list of options for the paging requests
    	    // paging request should not include the query parameters from the initial
    	    // request, but should include the headers.
    	    //List<Option> pagingOptions = new LinkedList<Option>();
    	   // pagingOptions.add(new HeaderOption("Prefer", "outlook.timezone=\"" + timeZone + "\""));

    	    while (messages != null) {
    	    	allMessages.clear();
    	        allMessages.addAll(messages.getCurrentPage());
    	        
    	        getAttachments(accessToken, allMessages);

    	        IMessageCollectionRequestBuilder nextPage =
    	            messages.getNextPage();

    	        if (nextPage == null) {
    	        	System.out.println("Finalizado.");
    	            break;
    	        } else {
    	            messages = nextPage
    	                .buildRequest()
    	                .get();
    	        }
    	    }
    	    return;
    	}
    public static void getAttachments(String accessToken, List<Message> Messages) {
    	System.out.println("Obteniendo archivos adjuntos...");
    	ensureGraphClient(accessToken);
    	List<Attachment> att = new LinkedList<Attachment>();
    	
    	IAttachmentCollectionPage allAttachments;
    	String from = "2021/02/22";
    	String to = "2021/02/24";
    	String path = "";
    	
    	JSONObject config = new JSONObject();
		JSONArray config_array = new JSONArray();
		JSONParser jsonParser = new JSONParser();
		
		
		try 
        {
			FileReader reader = new FileReader("config.json");
            //Read JSON file
            Object obj;
			try {
				obj = jsonParser.parse(reader);
				config_array = (JSONArray) obj;
	            System.out.println(config_array);   
			} catch (org.json.simple.parser.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 
            
            config = (JSONObject) config_array.get(0);
            from = (String) config.get("fecha_inicial");
            to = (String) config.get("fecha_final");
            path = (String) config.get("output_path");
        } catch (FileNotFoundException e) {
        	System.out.println("Error 145");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        }

    	FileWriter file_out;
    	
    	String file_name;
    	String decodedData;
    	
    	//byte[] decodedBytes;
    	for (Message message : Messages) {
    	   System.out.println(message.getRawObject().get("receivedDateTime").toString().substring(1,11).replace("-","/"));
    	   
		   DateFormat format = new SimpleDateFormat("yyyy/MM/dd"); 
		   try { 
			   Date inicial_date = format.parse(from); 
			   Date current_date = format.parse(message.getRawObject().get("receivedDateTime").toString().substring(1,11).replace("-","/")); 
			   Date final_date = format.parse(to);
			   if(current_date.after(final_date)) { 
				   continue; 
			   } 
			   else if(current_date.before(inicial_date)) 
			   { 
				   System.out.println("Final"); 
				   return; 
			   } 
		   }catch(ParseException e) {
			   System.out.println("Parsing Error"); 
		   }
    	   att.clear();
    	   allAttachments = graphClient.me().messages(message.id).attachments()
    				.buildRequest()
    				.get();
    	   //String hint = message.subject;
    	   //System.out.println("subject: "+ hint);
    	   if(allAttachments == null) {
    		   System.out.println("escape");
    		   return;
    	   }
    	   att.addAll(allAttachments.getCurrentPage());
    	   for(Attachment actual : att) {
    		 String requestUrl = graphClient
    	                .me()
    	                .messages(message.id)
    	                .attachments(actual.id)
    	                .buildRequest()
    	                .getRequestUrl()
    	                .toString();
    		  
    		 FileAttachmentRequestBuilder requestBuilder = new FileAttachmentRequestBuilder(requestUrl, graphClient, null);

    	     FileAttachment fileAttachment = requestBuilder.buildRequest().get();
    	     
    	     /*String actualData = fileAttachment.contentBytes.toString();
    	     byte[] bytes = fileAttachment.contentBytes.toString().getBytes(StandardCharsets.UTF_8);
    	     String utf8EncodedString = new String(bytes, StandardCharsets.UTF_8);

    	     assertEquals(actualData, utf8EncodedString);*/
    	     //System.out.print(fileAttachment.getRawObject().get("contentBytes").getAsString());
    		 decodedData = new String(Base64.getDecoder().decode(fileAttachment.getRawObject().get("contentBytes").getAsString()));
    		 //System.out.print(decodedData);
    	     //decodedBytes = fileAttachment.contentBytes;
    		 byte[] bytes = decodedData.getBytes(StandardCharsets.UTF_8);
    	     String utf8EncodedString = new String(bytes, StandardCharsets.UTF_8);
    		 file_name = fileAttachment.name;
    		 if(!file_name.contains(".xml")) {
    			 System.out.println("Hola");
    			 continue;
    		 }
    		 File new_file = new File(path+file_name);
    		 try {
    			if (new_file.createNewFile()) {
    				file_out = new FileWriter(path+file_name);
    				file_out.write(utf8EncodedString);
    				file_out.close();
    				System.out.println("Archivo Adjunto descargado: "+ file_name);
			    } else {
			       System.out.println("El Archivo ya existe.");
			    }
			} catch (IOException e) {
				System.out.println("Error...");
				e.printStackTrace();
			}
    	   }
           
        }
    	return;
    }
}
