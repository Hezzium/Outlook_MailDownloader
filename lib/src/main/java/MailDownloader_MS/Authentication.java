package MailDownloader_MS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.microsoft.aad.msal4j.DeviceCode;
import com.microsoft.aad.msal4j.DeviceCodeFlowParameters;
import com.microsoft.aad.msal4j.IAccount;
import com.microsoft.aad.msal4j.IAuthenticationResult;
//import com.microsoft.aad.msal4j.IPublicClientApplication;
//import com.microsoft.aad.msal4j.ITokenCache;
import com.microsoft.aad.msal4j.ITokenCacheAccessAspect;
import com.microsoft.aad.msal4j.ITokenCacheAccessContext;
import com.microsoft.aad.msal4j.PublicClientApplication;
import com.microsoft.aad.msal4j.SilentParameters;
import com.microsoft.aad.msal4j.TokenCache;
import com.microsoft.aad.msal4j.TokenCacheAccessContext;
//import com.microsoft.aad.msal4j.TokenCacheAccessContext.TokenCacheAccessContextBuilder;

//app.scopes=User.Read,MailboxSettings.Read,Calendars.ReadWrite,Mail.Read
public class Authentication {
	

	private static String applicationId;

	private static IAuthenticationResult authenticationResult;
    // Set authority to allow only organizational accounts
    // Device code flow only supports organizational accounts
    private final static String authority = "https://login.microsoftonline.com/common/";

    public static void initialize(String applicationId) {
        Authentication.applicationId = applicationId;
    }

    public static String getUserAccessTokenFirst(String[] scopes) {
        if (applicationId == null) {
            System.out.println("You must initialize Authentication before calling getUserAccessToken");
            return null;
        }

        Set<String> scopeSet = Set.of(scopes);
       
        ExecutorService pool = Executors.newFixedThreadPool(1);
        //PublicClientApplication app;
  
        PublicClientApplication app;
        try {
            // Build the MSAL application object with
            // app ID and authority
        	 app = PublicClientApplication.builder(applicationId)
                   .authority(authority)
                   .executorService(pool)
                   .build();
        } catch (MalformedURLException e) {
            return null;
        }
        
       

        // Create consumer to receive the DeviceCode object
        // This method gets executed during the flow and provides
        // the URL the user logs into and the device code to enter
		
		  Consumer<DeviceCode> deviceCodeConsumer = (DeviceCode deviceCode) -> { 
		  // Print the login information to the console
		  System.out.println(deviceCode.message()); };
		 
        // Request a token, passing the requested permission scopes
        
        authenticationResult = app.acquireToken(
        		DeviceCodeFlowParameters
                .builder(scopeSet, deviceCodeConsumer)
                .build()
        ).exceptionally(ex -> {
            System.out.println("Unable to authenticate - " + ex.getMessage());
            return null;
        }).join();
       
        String data = app.tokenCache().serialize();
        File cache_file = new File("file_cache.json"); 
        if (cache_file.delete()) { 
		     try {
		    	cache_file.createNewFile(); 
		    	FileWriter myWriter = new FileWriter("file_cache.json");
				myWriter.write(data);
				myWriter.flush();
			    myWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		     
        } else {
        	 try {
 		    	cache_file.createNewFile(); 
 		    	FileWriter myWriter = new FileWriter("file_cache.json");
 				myWriter.write(data);
 				myWriter.flush();
 			    myWriter.close();
 			} catch (IOException e) {
 				e.printStackTrace();
 			} 
        } 
        pool.shutdown();

        if (authenticationResult != null) {

            return authenticationResult.accessToken();
        }

        return null;
    }
    public static String getUserAccessTokenSilent(String[] scopes) { 
    	 if (applicationId == null) {
             System.out.println("You must initialize Authentication before calling getUserAccessToken");
             return null;
         }

         Set<String> scopeSet = Set.of(scopes);
        
         ExecutorService pool = Executors.newFixedThreadPool(1);
         //PublicClientApplication app;
         
         TokenCacheAccessContext token_cache;
         TokenCache first = new TokenCache();
         PublicClientApplication app;
         String data = "";
       
         try {
             File myObj = new File("file_cache.json");
             Scanner myReader = new Scanner(myObj);
             
             while (myReader.hasNextLine()) {
               data = myReader.nextLine();
             }
             //app.tokenCache().deserialize(data);
             myReader.close();
           } catch (FileNotFoundException e) {
             System.out.println("An error occurred.");
             e.printStackTrace();
           }
         //first.deserialize(data);
         token_cache = TokenCacheAccessContext.builder().tokenCache(first).build();
         ITokenCacheAccessAspect persistenceAspect = new TokenPersistence(data);
         persistenceAspect.beforeCacheAccess(token_cache);
         
         try {
             // Build the MSAL application object with
             // app ID and authority
         	 app = PublicClientApplication.builder(applicationId).setTokenCacheAccessAspect(persistenceAspect)
                    .authority(authority)
                    .executorService(pool)
                    .build();
         	//app.tokenCache().deserialize(token_cache.tokenCache().serialize());
         } catch (MalformedURLException e) {
             return null;
         }
         
         // Request a token, passing the requested permission scopes
         
         try {
        	CompletableFuture<Set<IAccount>> accs = app.getAccounts();
        	try {
				Set<IAccount> accs2 = accs.get();
				Iterator<IAccount> it = accs2.iterator();
			     while(it.hasNext()){
			        System.out.println(it.next().username());
			     }
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            System.out.println(app.tokenCache().serialize());
			authenticationResult = app.acquireTokenSilently(
					 SilentParameters
			         .builder(scopeSet)
			         .build()
			 ).exceptionally(ex -> {
			     System.out.println("Unable to authenticate - " + ex.getMessage());
			     return null;
			 }).join();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
         persistenceAspect.afterCacheAccess(token_cache);
         data = app.tokenCache().serialize();
         File cache_file = new File("file_cache.json"); 
         if (cache_file.delete()) { 
 		     try {
 		    	cache_file.createNewFile(); 
 		    	FileWriter myWriter = new FileWriter("file_cache.json");
 				myWriter.write(data);
 				myWriter.flush();
 			    myWriter.close();
 			} catch (IOException e) {
 				e.printStackTrace();
 			}
 		     
         } else {
         	 try {
  		    	cache_file.createNewFile(); 
  		    	FileWriter myWriter = new FileWriter("file_cache.json");
  				myWriter.write(data);
  				myWriter.flush();
  			    myWriter.close();
  			} catch (IOException e) {
  				e.printStackTrace();
  			} 
         } 

         pool.shutdown();

         if (authenticationResult != null) {

             return authenticationResult.accessToken();
         }
    	
    	return null;
    }
    static class TokenPersistence implements ITokenCacheAccessAspect {
    	String data;

    	TokenPersistence(String data) {
    	        this.data = data;
    	}

    	@Override
    	public void beforeCacheAccess(ITokenCacheAccessContext iTokenCacheAccessContext) {
    	        iTokenCacheAccessContext.tokenCache().deserialize(data);
    	}

    	@Override
    	public void afterCacheAccess(ITokenCacheAccessContext iTokenCacheAccessContext) {
    	        data = iTokenCacheAccessContext.tokenCache().serialize();
    	}
    }
}
