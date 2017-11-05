package com.kachundena.urlreader;

import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.*;

import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.*;

import javax.activation.MimetypesFileTypeMap;


public class URLReader {
    public static void main(String[] args) throws Exception {
        String szUrl = args[0];
        String szPath = args[1];

        Long iProject = insertProject(szUrl);

        analizeUrltoDb (szUrl, iProject, szPath);      
   }
    
   private static void analizeUrltoDb(String pszUrl, Long piProject, String pszPath) throws Exception {
        
        Document doc = Jsoup.connect(pszUrl).get();
        Elements links = doc.select("a[href]");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");
        
        Long iSite = getSite(piProject, pszUrl);
        if (iSite == 0) {
            iSite = insertSite(piProject, pszUrl);
            for (Element src : media) {
                Long iContent = getContent(piProject, iSite, src.attr("abs:src"));
                if (!getFileNameFromUrl(src.attr("abs:src")).equals(null) && !getFileNameFromUrl(src.attr("abs:src")).equals("")) {
                    if (getTypeSelect(getFileNameFromUrl(src.attr("abs:src"))) == true ) {
                        if (isFileExists(pszPath + getFileNameFromUrl(src.attr("abs:src"))) == 0)  {
                            saveFile(src.attr("abs:src"), getFileNameFromUrl(src.attr("abs:src")), pszPath);
                        }
                        if (iContent == 0) {
                            iContent = insertContent(piProject, iSite, src.attr("abs:src"), src.tagName());
                        }
                    }
                }
            }

            for (Element link : imports) {
                Long iContent = getContent(piProject, iSite, link.attr("abs:href"));
                if (!getFileNameFromUrl(link.attr("abs:href")).equals(null) && !getFileNameFromUrl(link.attr("abs:href")).equals("")) {
                    if (getTypeSelect(getFileNameFromUrl(link.attr("abs:href"))) == true ) {
                        if (isFileExists(pszPath + getFileNameFromUrl(link.attr("abs:href"))) == 0)  {
                            saveFile(link.attr("abs:href"), getFileNameFromUrl(link.attr("abs:href")), pszPath);
                        }
                        if (iContent == 0) {
                            iContent = insertContent(piProject, iSite, link.attr("abs:href"), "Media");
                        }
                    }
                }
            }

            for (Element link : links) {
                Long iContent = getContent(piProject, iSite, link.attr("abs:href"));
                if (iContent == 0) {
                    iContent = insertContent(piProject, iSite, link.attr("abs:href"), "Link");
                }
            }    
            
            for (Element link : links) {
                analizeUrltoDb(link.attr("abs:href"), piProject, pszPath);
            }          
        }        
    }
   
   private String getTypeFile(String pszFile) {
       String returnvalue = "";
       MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap(); 
       returnvalue = mimeTypesMap.getContentType(pszFile);
       return returnvalue;
       
   }
   
   private static boolean getTypeSelect(String pszFile ) {
       boolean returnvalue = false;
       if (pszFile.substring(pszFile.lastIndexOf('.') + 1) == "pdf" ||
           pszFile.substring(pszFile.lastIndexOf('.') + 1) == "jpg" ||
           pszFile.substring(pszFile.lastIndexOf('.') + 1) == "png" ||
           pszFile.substring(pszFile.lastIndexOf('.') + 1) == "jpeg") {
           returnvalue = true;
       }
       return returnvalue;
       
   }
   
   private static void saveFile(String pszFileUrl, String pszFileSave, String pszFileRoute) {
       URL url;
       try {
            url = new URL(pszFileUrl);
            String szFileSave = pszFileRoute + Paths.get(pszFileSave);
            InputStream in;
            in = url.openStream();
            Files.copy(in, Paths.get(szFileSave), StandardCopyOption.REPLACE_EXISTING);
            in.close();
       } 
       catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
       } 
       catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
       }       
   }
   
   private static String getFileNameFromUrl(String pszUrl) {
    try {
        String returnvalue = "";
        URL url;
        url = new URL(pszUrl);
        returnvalue = FilenameUtils.getName(url.getPath());
        return returnvalue;
    } catch (MalformedURLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return "";
    }
   }

   private static int isFileExists(String szFile) {
       int returnvalue = 0;
       File f = new File(szFile);
       if(f.exists() && !f.isDirectory()) { 
           returnvalue = 1;
       }
       return returnvalue;
   }
    
   private static void analizeUrltoTxt(String pszUrl, String pszPrefijo) throws Exception {
    	
        Document doc = Jsoup.connect(pszUrl).get();
        Elements links = doc.select("a[href]");
        Elements media = doc.select("[src]");
        Elements imports = doc.select("link[href]");
        
        //final String DIR_INI = "c:\\alex\\desarrollo\\";
        
        String szFileUrl = pszPrefijo + pszUrl.replaceAll("/", "-").replaceAll(":", "_") + ".txt";
        File f = new File(szFileUrl);
  	  	if(!f.exists()){
        
	        try {
	            FileWriter writer = new FileWriter(szFileUrl, true);
	            BufferedWriter bufferedWriter = new BufferedWriter(writer);
	
	            System.out.println("FICHERO: " + szFileUrl);
	            bufferedWriter.write("\nMedia: " + media.size());
	            for (Element src : media) {
	                if (src.tagName().equals("img")) {
	                	bufferedWriter.write(src.tagName() + ": " + src.attr("abs:src"));
	                }
	                else {
	                	bufferedWriter.write(src.tagName() + ": " + src.attr("abs:src"));
	                }
	                bufferedWriter.newLine();
	            }
	
	            bufferedWriter.write("\nImports: " + imports.size());
	            for (Element link : imports) {
	            	bufferedWriter.write(link.tagName() + " - " + link.attr("abs:href"));
	            	bufferedWriter.newLine();
	            }
	
	            bufferedWriter.write("\nLinks: " + links.size());
	            for (Element link : links) {
	            	bufferedWriter.write(link.attr("abs:href") + " - " + link.text());
	            	bufferedWriter.newLine();
	            }
	            bufferedWriter.close();
	            
	            /*for (Element link : links) {
		        	analizeUrl (link.attr("abs:href"));
		        }*/
	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
  	  	}

        
        
    }
   
	public static Connection connectDB() {
		Connection vconSQL = null;
		try {
			Class.forName("org.sqlite.JDBC");
			vconSQL = DriverManager.getConnection("jdbc:sqlite:urlreader.sqlite");
		} 
		catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		return vconSQL;
	}
	public static void disconnectDB(Connection pconSQL) {
		try {
			pconSQL.close();
		} 
		catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}
	
	
    public static Long insertProject(String pszProject) {
        Long returnvalue = (long) 0;
        Connection c = null;
        PreparedStatement stmt = null;
        try {
            c = connectDB();
            String sql = "INSERT INTO project (text) VALUES ('" + pszProject + "');";
            stmt = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                returnvalue = generatedKeys.getLong(1);
            }
            stmt.close();
            disconnectDB(c);
        } 
        catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            returnvalue = (long) -1;
            System.exit(0);
        }
        return returnvalue;
    }
	
	
	public static Long insertSite(Long piProject, String pszSite) {
		Long returnvalue = (long) 0;
		Connection c = null;
		PreparedStatement stmt = null;
		try {
			c = connectDB();
			String sql = "INSERT INTO site (project_id, text) VALUES (" + piProject + ",'" + pszSite + "');";
			stmt = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        stmt.executeUpdate();
	        ResultSet generatedKeys = stmt.getGeneratedKeys();
	        if (generatedKeys.next()) {
	            returnvalue = generatedKeys.getLong(1);
	        }
			stmt.close();
			disconnectDB(c);
		} 
		catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			returnvalue = (long) -1;
			System.exit(0);
		}
		return returnvalue;
	}

    public static Long insertContent(Long piProject, Long piSite, String pszContent, String pszType) {
        Long returnvalue = (long) 0;
        Connection c = null;
        PreparedStatement stmt = null;
        try {
            c = connectDB();
            String sql = "INSERT INTO content (project_id, site_id, text, type) VALUES (" + piProject + "," + piSite + ",'" + pszContent + "','" + pszType + "');";
            stmt = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                returnvalue = generatedKeys.getLong(1);
            }
            stmt.close();
            disconnectDB(c);
        } 
        catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            returnvalue = (long) -1;
            System.exit(0);
        }
        return returnvalue;
    }


    public static Long getSite(Long piProject, String pszSite) {
        Long returnvalue = (long) 0;
        Connection c = null;
        Statement stmt = null;
        try {
            c = connectDB();
            c.setAutoCommit(false);
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT site_id AS id FROM site WHERE text = '" + pszSite + 
                    "' AND project_id = " + piProject + " ;" );
            while ( rs.next() ) {
                returnvalue = rs.getLong("id");
            }
            rs.close();
            stmt.close();
            disconnectDB(c);
        } 
        catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        return returnvalue;
    }
    
    
	public static Long getContent(Long piProject, Long piSite, String pszContent ) {
		Long returnvalue = (long) 0;
		Connection c = null;
		Statement stmt = null;
		try {
			c = connectDB();
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT content_id AS id FROM content WHERE text = '"
			+ pszContent + "' AND site_id = " + piSite + " AND project_id = " + piProject + " ;" );
			while ( rs.next() ) {
				returnvalue = rs.getLong("id");
			}
			rs.close();
			stmt.close();
			disconnectDB(c);
		} 
		catch ( Exception e ) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
		return returnvalue;
	}
	
}
