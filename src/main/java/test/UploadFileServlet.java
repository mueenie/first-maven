package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Servlet implementation class UploadFileServlet
 */
public class UploadFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private StringBuffer outputStr;
	private File uploadedFile;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadFileServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		out.print("^_^, get?");
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(Ht pServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		outputStr = new StringBuffer();
		
		// set request encoding
		request.setCharacterEncoding("UTF-8");

		// judge if multipart request
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);

		// Create a factory for disk-based file items
		DiskFileItemFactory factory = new DiskFileItemFactory();

		// get servlet context// Configure a repository (to ensure a secure temp
		// location is used)
		ServletContext servletContext = this.getServletConfig()
				.getServletContext();
		File repository = (File) servletContext
				.getAttribute("javax.servlet.context.tempdir");
		factory.setRepository(repository);

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);

		// Parse the request
		List<FileItem> items = null;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Process the uploaded items
		Iterator<FileItem> iter = items.iterator();
		while (iter.hasNext()) {
			FileItem item = iter.next();

			if (item.isFormField()) {
				processFormField(item);
			} else {
				try {
					processUploadedFile1(item);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		//
		//processExcelFile();

		// output
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println("let's begin. ^_^" + "<br>");
		out.println("isMultipart? " + isMultipart + "<br>");
		out.println("tempdir: "
				+ servletContext.getAttribute("javax.servlet.context.tempdir")
				+ "<br>");
		out.println("files: " + items.size() + "<br>");
		out.println(outputStr.toString());
		out.close();
	}

	protected void processFormField(FileItem item) {
		// Process a regular form field
		if (item.isFormField()) {
		    String name = item.getFieldName();
		    String value = item.getString();
		    outputStr.append("<field>");
		    outputStr.append("name:");
		    outputStr.append(name);
		    outputStr.append(", ");
		    outputStr.append("value:");
		    outputStr.append(value);
		    outputStr.append("<br>");
		}
	}

	protected void processUploadedFile1(FileItem item) {
		//String filePath = "D:\\bbb.csv";
        BufferedReader bufferedReader = null;
        Connection  conn = null;
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/test";
        try {
            Class.forName(driver);
             conn =  DriverManager.getConnection(url, "root", "Rabbit02");
     
//             bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
             bufferedReader = new BufferedReader(new InputStreamReader(item.getInputStream(),"ISO8859-1"));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                String[] columns = line.split(",");
                if(columns[0].trim().equalsIgnoreCase("序号")){
                    continue;
                }
                System.out.println(columns[0]);
                System.out.println(columns[1]);
                System.out.println(columns[2]);
                System.out.println(columns[3]);
                System.out.println(columns[4]);
                System.out.println(columns[5]);
                System.out.println(columns[6]);
                 PreparedStatement pstmt = conn.prepareStatement("insert into test(xh,kaohao,name,sex,sfz,zy,xf)values(?,?,?,?,?,?,?)");
                 pstmt.setString(1, columns[0]);
                 pstmt.setString(2, columns[1]);
                 pstmt.setString(3, columns[2]);
                 pstmt.setString(4, columns[3]);
                 pstmt.setString(5, columns[4]);
                 pstmt.setString(6, columns[5]);
                 pstmt.setString(7, columns[6]);
                 pstmt.executeUpdate();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            try {
                conn.close();
                bufferedReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
	protected void processUploadedFile(FileItem item) throws Exception{
		// Process a file upload
		String fileName = new String();
		if (!item.isFormField()) {
		    String fieldName = item.getFieldName();
		    fileName = item.getName();
		    String contentType = item.getContentType();
		    boolean isInMemory = item.isInMemory();
		    long sizeInBytes = item.getSize();
		    outputStr.append("<file>");
		    outputStr.append("fieldName:");
		    outputStr.append(fieldName);
		    outputStr.append(", ");
		    outputStr.append("fileName:");
		    outputStr.append(fileName);
		    outputStr.append(", ");
		    outputStr.append("contentType:");
		    outputStr.append(contentType);
		    outputStr.append(", ");
		    outputStr.append("isInMemory:");
		    outputStr.append(isInMemory);
		    outputStr.append(", ");
		    outputStr.append("sizeInBytes:");
		    outputStr.append(sizeInBytes);
		    outputStr.append("<br>");
		}
		
		// Process a file upload
		boolean writeToFile = true;
//		String filename = "newfile.jar";
		if (writeToFile) {
		    uploadedFile = new File(fileName);
		    item.write(uploadedFile);
		} else {
		    InputStream uploadedStream = item.getInputStream();
		    // ...
		    uploadedStream.close();
		}
	}
	
	//
	protected void processExcelFile() throws FileNotFoundException{
	    InputStream inp = new FileInputStream(uploadedFile);

	}
	

}
