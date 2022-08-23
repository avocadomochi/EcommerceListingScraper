import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SheetsInteracter {
    private static Sheets buysAndResells;
    private static String APPLICATION_NAME = [YOUR APPLICATION NAME];
    private static String SPREADSHEET_ID = [YOUR SPREADSHEET ID];
    private static String SHEET_NAME = [YOUR SHEET NAME YOU WOULD LIKE TO EDIT];

    // proper authorization and getSheetsService from the Java Google Sheets API guide

    private static Credential authorize() throws IOException, GeneralSecurityException {
        InputStream in = SheetsInteracter.class.getResourceAsStream("/credentials.json");
         GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
           GsonFactory.getDefaultInstance(), new InputStreamReader(in)
         );

         List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);

         GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                 GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(),
                 clientSecrets, scopes)
                 .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                 .setAccessType("offline")
                 .build();
         Credential credential = new AuthorizationCodeInstalledApp(
                 flow, new LocalServerReceiver())
                 .authorize("user");

         return credential;
    }

    public static Sheets getSheetsService() throws IOException, GeneralSecurityException {
        Credential credential = authorize();
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(), credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static void main(String[] args) throws IOException, GeneralSecurityException {
        buysAndResells = getSheetsService();

        // creates empty ArrayList of Arrays and sets it as Scraper.listingData()
        ArrayList<String[]> data = new ArrayList<String[]>();
        data = Scraper.listingData();

        // for each Array in ArrayList, creates a ValueRange() and appends it to your sheet
        for(String i[] : data) {
            ValueRange dataRange = new ValueRange()
                    .setValues(Arrays.asList(
                            Arrays.asList((Object[])i)
                    ));

            buysAndResells.spreadsheets().values()
                    .append(SPREADSHEET_ID, SHEET_NAME, dataRange)
                    .setValueInputOption("USER_ENTERED")
                    .setInsertDataOption("INSERT_ROWS")
                    .setIncludeValuesInResponse(true)
                    .execute();
        }
    }
}