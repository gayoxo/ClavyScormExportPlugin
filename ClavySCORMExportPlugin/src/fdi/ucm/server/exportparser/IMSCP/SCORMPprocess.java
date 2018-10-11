/**
 * 
 */
package fdi.ucm.server.exportparser.IMSCP;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.management.RuntimeErrorException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import fdi.ucm.server.modelComplete.collection.CompleteCollection;
import fdi.ucm.server.modelComplete.collection.CompleteLogAndUpdates;
import fdi.ucm.server.modelComplete.collection.document.CompleteDocuments;
import fdi.ucm.server.modelComplete.collection.document.CompleteElement;
import fdi.ucm.server.modelComplete.collection.document.CompleteFile;
import fdi.ucm.server.modelComplete.collection.document.CompleteLinkElement;
import fdi.ucm.server.modelComplete.collection.document.CompleteResourceElementFile;
import fdi.ucm.server.modelComplete.collection.document.CompleteResourceElementURL;
import fdi.ucm.server.modelComplete.collection.document.CompleteTextElement;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteElementType;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteGrammar;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteLinkElementType;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteOperationalValueType;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteResourceElementType;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteTextElementType;


/**
 * @author Joaquin Gayoso-Cabada
 *
 */
public class SCORMPprocess {


	protected static final String EXPORTTEXT = "Export HTML RESULT";
	protected List<Long> DocumentoT;
	protected CompleteCollection Salvar;
	protected String SOURCE_FOLDER;
	protected CompleteLogAndUpdates CL;
	private static final Pattern regexAmbito = Pattern.compile("^(ht|f)tp(s)*://(.)*$");
	protected HashMap<String,CompleteElementType> NameCSS;
	private Element metadata;
	private Element organizations;
	private Element resources;
	private HashMap<String, String> Recursos;
	private HashMap<String,CompleteDocuments> RecursosP;
	private int contadorRec;
	private String TextoEntrada;
	private int contadorFiles;
	protected static final String CLAVY="OdAClavy";
	private HashMap<CompleteDocuments, HashSet<CompleteDocuments>> RecursosQ;
	protected HashMap<Long,String> TablaHTML;
	protected HashMap<Long,HashSet<CompleteDocuments>> TablaHTMLLink;
	private HashSet<CompleteDocuments> ProcesadosGeneral;
	private int contador_IDs;
//	private HashMap<CompleteGrammar, HashSet<CompleteDocuments>> Gram_doc;
	private int nummap;
private String IDBase;
private CompleteGrammar Quizz;
private Element imssssequencingCollection;
private String IDQUEST;
private int counter=0;

	public SCORMPprocess(List<Long> listaDeDocumentos, CompleteCollection salvar, String sOURCE_FOLDER, CompleteLogAndUpdates cL, String entradaText) {
		
		
		DocumentoT=listaDeDocumentos;
		Salvar=salvar;
		SOURCE_FOLDER=sOURCE_FOLDER;
		CL=cL;
		TextoEntrada=entradaText;
		NameCSS=new HashMap<String,CompleteElementType>();
		TablaHTML=new HashMap<Long, String>();
		TablaHTMLLink=new HashMap<Long, HashSet<CompleteDocuments>>();
		nummap=0;
	}

	public void preocess() {
		
		ProcesadosGeneral=new HashSet<CompleteDocuments>();
		
		String SN=Long.toString(System.nanoTime());
		
		LinkedList<CompleteDocuments> completeDocuments = new LinkedList<CompleteDocuments>();
		
		for (CompleteDocuments docuemntos : Salvar.getEstructuras())
			if (DocumentoT.isEmpty()||DocumentoT.contains(docuemntos.getClavilenoid()))
			completeDocuments.addFirst(docuemntos);
	
		
		

		if (completeDocuments.isEmpty())
			{
			CL.getLogLines().add("Error, documento no existe");
			return;
			}
		
		Recursos=new HashMap<String,String>();
		RecursosP= new HashMap<>();
		RecursosQ=new HashMap<CompleteDocuments, HashSet<CompleteDocuments>>();
		try {
			
			contadorRec=0;
			contadorFiles=0;
			contador_IDs=0;
			
			
			IDBase="com.medpix.clavy."+SN+".sequencing.randomtest";
			IDQUEST="com.medpix.clavy."+SN+".interactions";
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        DOMImplementation implementation = builder.getDOMImplementation();
	        Document document = implementation.createDocument(null, "manifest", null);
	        document.setXmlVersion("1.0");
	        
	        ArrayList<CompleteGrammar> GramaticasAProcesar=ProcesaGramaticas(Salvar.getMetamodelGrammar());
	        
	        Element manifest = document.getDocumentElement();
	        
	        {
			    Attr Atr = document.createAttribute("identifier");
			    Atr.setValue(IDBase+".20043rd");
			    manifest.setAttributeNode(Atr);
			    }
	        
	        
	       
		        
	        {
			    Attr Atr = document.createAttribute("xmlns:xsi");
			    Atr.setValue("http://www.w3.org/2001/XMLSchema-instance");
			    manifest.setAttributeNode(Atr);
			    }
	        
	        {
			    Attr Atr = document.createAttribute("xmlns:adlcp");
			    Atr.setValue("http://www.adlnet.org/xsd/adlcp_v1p3");
			    manifest.setAttributeNode(Atr);
			    }
	        
	        {
			    Attr Atr = document.createAttribute("xmlns:adlseq");
			    Atr.setValue("http://www.adlnet.org/xsd/adlseq_v1p3");
			    manifest.setAttributeNode(Atr);
			    }
		        
	        
		        {
			    Attr Atr = document.createAttribute("xmlns:adlnav");
			    Atr.setValue("http://www.adlnet.org/xsd/adlnav_v1p3");
			    manifest.setAttributeNode(Atr);
			    }
		        
		        {
				    Attr Atr = document.createAttribute("xmlns:imsss");
				    Atr.setValue("http://www.imsglobal.org/xsd/imsss");
				    manifest.setAttributeNode(Atr);
				    }

		       
		        {
			    Attr Atr = document.createAttribute("xsi:schemaLocation");
			    Atr.setValue("http://www.imsglobal.org/xsd/imscp_v1p1 imscp_v1p1.xsd "+
                             "http://www.adlnet.org/xsd/adlcp_v1p3 adlcp_v1p3.xsd "+
                             "http://www.adlnet.org/xsd/adlseq_v1p3 adlseq_v1p3.xsd "+
                             "http://www.adlnet.org/xsd/adlnav_v1p3 adlnav_v1p3.xsd "+
                             "http://www.imsglobal.org/xsd/imsss imsss_v1p0.xsd");
			    manifest.setAttributeNode(Atr);
			    }
		        
		       
		        {
			        Attr Atr = document.createAttribute("xmlns");
			        Atr.setValue("http://www.imsglobal.org/xsd/imscp_v1p1");
			        manifest.setAttributeNode(Atr);
			        }
		        
		        {
				Attr Atr = document.createAttribute("version");
				Atr.setValue("1");
				manifest.setAttributeNode(Atr);
				}
	        
	        metadata = document.createElement("metadata"); 
	        organizations = document.createElement("organizations"); 
	        resources = document.createElement("resources"); 
	        imssssequencingCollection = document.createElement("imsss:sequencingCollection"); 
	        
	        manifest.appendChild(metadata);
	        manifest.appendChild(organizations);
	        manifest.appendChild(resources);
	        manifest.appendChild(imssssequencingCollection);
	        
	        processMetadata(document);
	        String Main_S=processOrganization(completeDocuments,document,GramaticasAProcesar);
	        {
				Attr Atr = document.createAttribute("default");
				Atr.setValue(Main_S);
				organizations.setAttributeNode(Atr);
			}
	        processResources(document);
	        creaJS();
	        creaJSTest();
	        processSecuancing(document);
	        processFilesBase();
	        
	        //Generate XML
            Source source = new DOMSource(document);
            //Indicamos donde lo queremos almacenar
            Result result = new StreamResult(new java.io.File(SOURCE_FOLDER+File.separator+"imsmanifest.xml")); //nombre del archivo
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(
                    "{http://xml.apache.org/xslt}indent-amount", "3");
            transformer.transform(source, result);
	        
		} catch (Exception e) {
			e.printStackTrace();
			CL.getLogLines().add(e.getLocalizedMessage());
		}
		
		
		
		try {
			IncludeJSMAPS();
		} catch (IOException e) {
			CL.getLogLines().add(e.getLocalizedMessage());
			e.printStackTrace();
		}
		creaLACSS();
		
		
	}


	
	private void processFilesBase() {

		String[] Lista={"adlcp_v1p3.xsd",
				"adlnav_v1p3.xsd",
				"adlseq_v1p3.xsd",
				"datatypes.dtd",
				"ims_xml.xsd",
				"imscp_v1p1.xsd",
				"imsss_v1p0.xsd",
				"imsss_v1p0auxresource.xsd",
				"imsss_v1p0control.xsd",
				"imsss_v1p0delivery.xsd",
				"imsss_v1p0limit.xsd",
				"imsss_v1p0objective.xsd",
				"imsss_v1p0random.xsd",
				"imsss_v1p0rollup.xsd",
				"imsss_v1p0seqrule.xsd",
				"imsss_v1p0util.xsd",
				"lom.xsd",
				"xml.xsd",
				"XMLSchema.dtd"};
		
		
		for (String string : Lista) {

//			try {
//			URL url = this.getClass().getResource("/staticfiles/"+string);
//			
//				copyFileUsingStream(url.openStream(), new File(SOURCE_FOLDER+File.separatorChar+string));
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			
//			try {
//			InputStream url = SCORMPprocess.class.getResourceAsStream("/staticfiles/"+string);
//			
//				copyFileUsingStream(url, new File(SOURCE_FOLDER+File.separatorChar+string));
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			InputStream url = null;
			try {
				url = SCORMPprocess.class.getResourceAsStream("staticfiles/"+string);

					copyFileUsingStream(url, new File(SOURCE_FOLDER+File.separatorChar+string));
					
				} catch (Exception e) {
					e.printStackTrace();
					InputStream url2 = null;
					try {
						url2 = new URL("http://clavy.fdi.ucm.es/Clavy2Data/jars/staticfiles/"+string).openStream();

							copyFileUsingStream(url2, new File(SOURCE_FOLDER+File.separatorChar+string));
							
						} catch (Exception e2) {
							e2.printStackTrace();
							
							
							
						}finally {
							try {
								url2.close();
							} catch (IOException e2){
								e2.printStackTrace();
							}
						}
				}
			finally {
				try {
					url.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
				
			
		}
		
		
		File sha= new File(SOURCE_FOLDER+File.separatorChar+"shared");
		sha.mkdirs();
		
		String[] ListaS={"assessmenttemplate.html",
				"background.jpg",
				"cclicense.png",
				"contentfunctions.js",
				"launchpage.html",
				"scormfunctions.js",
				"style.css"};
		
		
		for (String string : ListaS) {
//			URL url = this.getClass().getResource("/staticfiles/"+string);
			InputStream url = null;
			try {
				url = SCORMPprocess.class.getResourceAsStream("staticfiles/shared/"+string);
				copyFileUsingStream(url, new File(SOURCE_FOLDER+File.separatorChar+"shared"+File.separatorChar+string));
			} catch (Exception e) {
				e.printStackTrace();
				InputStream url2 = null;
				try {
					url2 = new URL("http://clavy.fdi.ucm.es/Clavy2Data/jars/staticfiles/shared/"+string).openStream();

						copyFileUsingStream(url2, new File(SOURCE_FOLDER+File.separatorChar+string));
						
					} catch (Exception e2) {
						e2.printStackTrace();
						
						
						
					}finally {
						try {
							url2.close();
						} catch (IOException e2){
							e2.printStackTrace();
						}
					}
			}
			finally {
				try {
					url.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		File light= new File(SOURCE_FOLDER+File.separatorChar+"lightbox");
		light.mkdirs();
		light= new File(SOURCE_FOLDER+File.separatorChar+"lightbox"+File.separatorChar+"css");
		light.mkdirs();
		light= new File(SOURCE_FOLDER+File.separatorChar+"lightbox"+File.separatorChar+"images");
		light.mkdirs();
		light= new File(SOURCE_FOLDER+File.separatorChar+"lightbox"+File.separatorChar+"js");
		light.mkdirs();
		
		String[] ListaL={"css/lightbox.css",
				"css/lightbox.min.css",
				"images/close.png",
				"images/loading.gif",
				"images/next.png",
				"images/prev.png",
				"js/lightbox-plus-jquery.js",
				"js/lightbox-plus-jquery.min.js",
				"js/lightbox-plus-jquery.min.map",
				"js/lightbox.js",
				"js/lightbox.min.js",
				"js/lightbox.min.map"};
		
		
		
		
		for (String string : ListaL) {
//			URL url = this.getClass().getResource("/staticfiles/"+string);
			InputStream url = null;
			try {
				url = SCORMPprocess.class.getResourceAsStream("staticfiles/lightbox/"+string);
				copyFileUsingStream(url, new File(SOURCE_FOLDER+File.separatorChar+"lightbox"+File.separatorChar+string));
			} catch (Exception e) {
				e.printStackTrace();
				InputStream url2 = null;
				try {
					url2 = new URL("http://clavy.fdi.ucm.es/Clavy2Data/jars/staticfiles/lightbox/"+string).openStream();

						copyFileUsingStream(url2, new File(SOURCE_FOLDER+File.separatorChar+string));
						
					} catch (Exception e2) {
						e2.printStackTrace();
						
						
						
					}finally {
						try {
							url2.close();
						} catch (IOException e2){
							e2.printStackTrace();
						}
					}
			}
			finally {
				try {
					url.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		
	}


	private static void copyFileUsingStream(InputStream source, File dest) throws IOException {
	    InputStream is = source;
	    OutputStream os = null;
	    try {
//	        is = new FileInputStream(source);
	        os = new FileOutputStream(dest);
	        byte[] buffer = new byte[1024];
	        int length;
	        while ((length = is.read(buffer)) > 0) {
	            os.write(buffer, 0, length);
	        }
	    } finally {
	        is.close();
	        os.close();
	    }
	}
	
	private void creaJSTest() {
		//RecursosQ
		if (Quizz!=null)
		for (Entry<CompleteDocuments, HashSet<CompleteDocuments>> entry2 : RecursosQ.entrySet()) {
			FileWriter filewriter = null;
			 PrintWriter printw = null;
			    
			 
			try {
				 filewriter = new FileWriter(SOURCE_FOLDER+File.separator+"q"+entry2.getKey().getClavilenoid()+"_questions.js");//declarar el archivo
			     printw = new PrintWriter(filewriter);//declarar un impresor
			          
			     
			     for (CompleteDocuments preguntaE : entry2.getValue()) {
			     
			     List<Long> ListaOpciones=getOptions(Quizz.getSons());
			     int solucion=getSolution(Quizz.getSons(),preguntaE.getDescription());
			     String pregunta=getQuestion(Quizz.getSons(),preguntaE.getDescription());
			     String Imagen=getImage(Quizz.getSons(),preguntaE.getDescription());
			     
			     List<CompleteElement> OpcioneDesorden=new ArrayList<CompleteElement>(); 
			     
			     for (CompleteElement elem : preguntaE.getDescription()) 
					if (ListaOpciones.contains(elem.getHastype().getClavilenoid()))
						OpcioneDesorden.add(elem);
				
			     List<CompleteElement> OpcioneOrden=new ArrayList<CompleteElement>(); 
			     
			     for (Long completeElement : ListaOpciones) 
					for (CompleteElement completeElement2 : OpcioneDesorden) 
						if (completeElement2.getHastype().getClavilenoid().equals(completeElement))
							{
							OpcioneOrden.add(completeElement2);
							break;
							}
					
			   
			     
			    	 printw.println(" test.AddQuestion( new Question (\""+IDQUEST+".q"+preguntaE.getClavilenoid()+"_1\",");
					 printw.println(" \""+pregunta+"\",");
					 printw.println(" QUESTION_TYPE_CHOICE,");
					 
					 boolean primer=true;
					 printw.print("new Array(");
					for (CompleteElement completeElement : OpcioneOrden) {
						if (!primer)
							printw.print(",");	
						else
							primer=false;
						
						 if (completeElement instanceof CompleteTextElement)
							 printw.print("\""+((CompleteTextElement)completeElement).getValue()+"\"");
					}
					printw.println("),");				
					
					String SoluS="";
					if (solucion>-1)
						if (solucion<=OpcioneOrden.size()&&(OpcioneOrden.get(solucion) instanceof CompleteTextElement))
								 SoluS=((CompleteTextElement)OpcioneOrden.get(solucion)).getValue();
						else;
					else
						System.out.println("No solucion for D="+preguntaE.getClavilenoid());
					printw.println(" \""+SoluS+"\",");
					 
					printw.println(" \"obj_playing\","); 
					printw.println(" \""+Imagen+"\")"); 
					printw.println(");"); 
				     
				    
				}
			     
			     printw.close();//cerramos el archivo  
			     
			     
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeErrorException(new Error(e), "Error de archivo");
			} 
		}
		
		
		
	}

	private String getImage(List<CompleteElementType> elemtq, List<CompleteElement> description) {
		for (CompleteElementType completeElementt : elemtq) {
			if (IsImage(completeElementt.getShows()))
				{
				for (CompleteElement completeElement : description) {
					if (completeElement.getHastype().equals(completeElementt))
						{
						if (completeElement instanceof CompleteResourceElementURL)
							return  ((CompleteResourceElementURL) completeElement).getValue();
						
						//TODO AQUI REVISAR COMO ESTA PORQUEIGUAL EL PATH ES RARO
						if (completeElement instanceof CompleteResourceElementFile)
							return  ((CompleteResourceElementFile) completeElement).getValue().getPath();
						
						if (completeElement instanceof CompleteTextElement)
							return  ((CompleteTextElement) completeElement).getValue();
						}
				}
				return "";
				}
			else
				{
					for (CompleteElementType completeElement : completeElementt.getSons()) {
						String Salida = getImage(completeElement.getSons(), description);
						if (Salida!=null)
							return Salida;
					}
				}
		}
		return "";
	}

	private boolean IsImage(ArrayList<CompleteOperationalValueType> shows) {
		for (CompleteOperationalValueType completeOperationalValueType : shows) {
			if (completeOperationalValueType.getView().toLowerCase().equals("SCORM".toLowerCase())&&completeOperationalValueType.getName().toLowerCase().equals("qimage".toLowerCase()))
				try {
					boolean Salida = Boolean.parseBoolean(completeOperationalValueType.getDefault().toLowerCase());
					return Salida;
				} catch (Exception e) {
					// TODO: handle exception
				}
				
		}
		return false;
	}

	private List<Long> getOptions(ArrayList<CompleteElementType> elemtq) {
		CompleteElementType OptionsS=null;
		for (CompleteElementType completeElementt : elemtq)
			if (IsOptions(completeElementt.getShows()))
				{
				if (completeElementt.getClassOfIterator()!=null)
					OptionsS=completeElementt.getClassOfIterator();
				else
					OptionsS=completeElementt;
				break;
				}
		
		List<Long> Salida=new ArrayList<>();
		for (CompleteElementType completeElementType : elemtq)
			if (completeElementType.getClassOfIterator()==OptionsS||completeElementType==OptionsS)
				Salida.add(completeElementType.getClavilenoid());
		
		
		return Salida;
	}

	

	private int getSolution(List<CompleteElementType> elemtq, List<CompleteElement> description) {
		for (CompleteElementType completeElementt : elemtq) {
			if (IsAnswer(completeElementt.getShows()))
				{
				for (CompleteElement completeElement : description) {
					if ((completeElement instanceof CompleteTextElement)
						&&completeElement.getHastype().equals(completeElementt))
						{
						try {
							String SolS=  ((CompleteTextElement) completeElement).getValue();
							Integer Sol=Integer.parseInt(SolS);
							return Sol;
						} catch (Exception e) {
							// TODO: handle exception
						}
						}
				}
				return -1;
				}
			else
				{
					for (CompleteElementType completeElement : completeElementt.getSons()) {
						int Salida = getSolution(completeElement.getSons(), description);
						if (Salida>0)
							return Salida;
					}
				}
		}
		return -1;
	}

	private String getQuestion(List<CompleteElementType> elemtq, List<CompleteElement> description) {
		
		for (CompleteElementType completeElementt : elemtq) {
			if (IsQuestion(completeElementt.getShows()))
				{
				for (CompleteElement completeElement : description) {
					if ((completeElement instanceof CompleteTextElement)
						&&completeElement.getHastype().equals(completeElementt))
						{
						return  ((CompleteTextElement) completeElement).getValue();
						}
				}
				return "no text";
				}
			else
				{
					for (CompleteElementType completeElement : completeElementt.getSons()) {
						String Salida = getQuestion(completeElement.getSons(), description);
						if (Salida!=null)
							return Salida;
					}
				}
		}
		return "no text";
	}

	private boolean IsQuestion(ArrayList<CompleteOperationalValueType> shows) {
		for (CompleteOperationalValueType completeOperationalValueType : shows) {
			if (completeOperationalValueType.getView().toLowerCase().equals("SCORM".toLowerCase())&&completeOperationalValueType.getName().toLowerCase().equals("question".toLowerCase()))
				try {
					boolean Salida = Boolean.parseBoolean(completeOperationalValueType.getDefault().toLowerCase());
					return Salida;
				} catch (Exception e) {
					// TODO: handle exception
				}
				
		}
		return false;
	}
	
	
	private boolean IsOptions(ArrayList<CompleteOperationalValueType> shows) {
		for (CompleteOperationalValueType completeOperationalValueType : shows) {
			if (completeOperationalValueType.getView().toLowerCase().equals("SCORM".toLowerCase())&&completeOperationalValueType.getName().toLowerCase().equals("options".toLowerCase()))
				try {
					boolean Salida = Boolean.parseBoolean(completeOperationalValueType.getDefault().toLowerCase());
					return Salida;
				} catch (Exception e) {
					// TODO: handle exception
				}
				
		}
		return false;
	}
	
	private boolean IsAnswer(ArrayList<CompleteOperationalValueType> shows) {
		for (CompleteOperationalValueType completeOperationalValueType : shows) {
			if (completeOperationalValueType.getView().toLowerCase().equals("SCORM".toLowerCase())&&completeOperationalValueType.getName().toLowerCase().equals("Answer".toLowerCase()))
				try {
					boolean Salida = Boolean.parseBoolean(completeOperationalValueType.getDefault().toLowerCase());
					return Salida;
				} catch (Exception e) {
					// TODO: handle exception
				}
				
		}
		return false;
	}

	private void creaJS() {
		FileWriter filewriter = null;
		 PrintWriter printw = null;
		    
		 
		 new File(SOURCE_FOLDER+"\\shared").mkdirs();
		try {
			 filewriter = new FileWriter(SOURCE_FOLDER+"\\shared\\pagerArray.js");//declarar el archivo
		     printw = new PrintWriter(filewriter);//declarar un impresor
		          
		     printw.println(" function setArray(queryString) {   ");
		     printw.println("var pageArray = new Array();");
		     printw.println("switch (queryString){");
		     
		     
		     for (Entry<String, CompleteDocuments> long1 : RecursosP.entrySet()) {
		    	String ValorURL = Recursos.get(long1.getKey());
		    	if (ValorURL!=null)
		    	{
		    		 printw.println("case \""+long1.getValue().getClavilenoid()+"\":");
		    		 printw.println(" var pageArray = new Array(1);");
		    		 printw.println("pageArray[0] = \""+ValorURL+"\";");
		    		 printw.println("break;");
		    	}
		    	
			}
		     
		     
		     /*
		      * 
		      * 		for (Entry<CompleteDocuments, HashSet<CompleteDocuments>> entry2 : RecursosQ.entrySet()) {
			FileWriter filewriter = null;
			 PrintWriter printw = null;
			    
			 
			try {
				 filewriter = new FileWriter(SOURCE_FOLDER+File.separator+"q"+entry2.getKey().getClavilenoid()+"_questions.js");//declarar el archivo
		      * 
		      */
		     
		     for (Entry<CompleteDocuments, HashSet<CompleteDocuments>> entry2 : RecursosQ.entrySet()) {

			    		 printw.println("case \"q"+entry2.getKey().getClavilenoid()+"\":");
			    		 printw.println(" var pageArray = new Array(1);");
			    		 printw.println("pageArray[0] = \"shared/assessmenttemplate.html?questions=q"+entry2.getKey().getClavilenoid()+"\";");
			    		 printw.println("break;");

			}

		     
		     printw.println("default:");
		     printw.println("alert(\"error, no match for '\" + queryString + \"'\");");
		     printw.println("break;");
		     printw.println("}");
		     printw.println("return pageArray;");
		     printw.println("}");
		     
		     printw.close();//cerramos el archivo
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeErrorException(new Error(e), "Error de archivo");
		} 
		
	}

	private void processSecuancing(Document document) {
		Element imssssequencing = document.createElement("imsss:sequencing"); 
		imssssequencingCollection.appendChild(imssssequencing);
		{
	        Attr Atr = document.createAttribute("ID");
	        Atr.setValue("test_sequencing_rules");
	        imssssequencing.setAttributeNode(Atr);
	        }
		
		Element imssssequencingRules = document.createElement("imsss:sequencingRules"); 
		imssssequencing.appendChild(imssssequencingRules);
		
		Element imssspostConditionRule = document.createElement("imsss:postConditionRule"); 
		imssssequencingRules.appendChild(imssspostConditionRule);
		
		Element imsssruleConditions = document.createElement("imsss:ruleConditions"); 
		imssspostConditionRule.appendChild(imsssruleConditions);
		
		Element imsssruleCondition = document.createElement("imsss:ruleCondition"); 
		imsssruleConditions.appendChild(imsssruleCondition);
		
		{
	        Attr Atr = document.createAttribute("condition");
	        Atr.setValue("always");
	        imsssruleCondition.setAttributeNode(Atr);
	        }
		
		Element imsssruleAction = document.createElement("imsss:ruleAction"); 
		imssspostConditionRule.appendChild(imsssruleAction);
		
		{
	        Attr Atr = document.createAttribute("action");
	        Atr.setValue("exitParent");
	        imsssruleAction.setAttributeNode(Atr);
	        }
		
		
		Element imsssobjectives = document.createElement("imsss:objectives"); 
		imssssequencing.appendChild(imsssobjectives);
		
		Element imssprimaryObjective = document.createElement("imsss:primaryObjective"); 
		imsssobjectives.appendChild(imssprimaryObjective);
		
		{
	        Attr Atr = document.createAttribute("objectiveID");
	        Atr.setValue("course_score");
	        imsssobjectives.setAttributeNode(Atr);
	        }
		
		Element imssmapInfo = document.createElement("imsss:mapInfo"); 
		imssprimaryObjective.appendChild(imssmapInfo);
		
		{
	        Attr Atr = document.createAttribute("targetObjectiveID");
	        Atr.setValue(IDBase+".course_score");
	        imssprimaryObjective.setAttributeNode(Atr);
	        }
		
		{
	        Attr Atr = document.createAttribute("readSatisfiedStatus");
	        Atr.setValue("false");
	        imssprimaryObjective.setAttributeNode(Atr);
	        }
		
		{
	        Attr Atr = document.createAttribute("readNormalizedMeasure");
	        Atr.setValue("false");
	        imssprimaryObjective.setAttributeNode(Atr);
	        }
		
		{
	        Attr Atr = document.createAttribute("writeNormalizedMeasure");
	        Atr.setValue("true");
	        imssprimaryObjective.setAttributeNode(Atr);
	        }
		
		
		Element imssdeliveryControls = document.createElement("imsss:deliveryControls"); 
		imssssequencing.appendChild(imssdeliveryControls);
		
		{
	        Attr Atr = document.createAttribute("completionSetByContent");
	        Atr.setValue("true");
	        imssdeliveryControls.setAttributeNode(Atr);
	        }
		
		{
	        Attr Atr = document.createAttribute("objectiveSetByContent");
	        Atr.setValue("true");
	        imssdeliveryControls.setAttributeNode(Atr);
	        }
		
	}

	private void IncludeJSMAPS() throws IOException {
		URL website = new URL("https://raw.githubusercontent.com/HPNeo/gmaps/master/gmaps.js");
		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		FileOutputStream fos = new FileOutputStream(SOURCE_FOLDER+File.separator+"gmaps.js");
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
		
	}

	private void processResources(Document document) {
		for (Entry<String, String> recursotable : Recursos.entrySet()) {
			Element ResourceUni = document.createElement("resource"); 
			resources.appendChild(ResourceUni);
			{
		        Attr Atr = document.createAttribute("identifier");
		        Atr.setValue(recursotable.getKey());
		        ResourceUni.setAttributeNode(Atr);
		        }
		        
		        {
			    Attr Atr = document.createAttribute("type");
			    Atr.setValue("webcontent");
			    ResourceUni.setAttributeNode(Atr);
			    }
		       
		        

			        Attr href = document.createAttribute("href");
			        href.setValue(recursotable.getValue());
			        ResourceUni.setAttributeNode(href);

			        
		        Element FileUni = document.createElement("file"); 
		        ResourceUni.appendChild(FileUni);
		        
		        {
			        Attr Atr = document.createAttribute("href");
			        Atr.setValue(recursotable.getValue());
			        FileUni.setAttributeNode(Atr);
			        }

		        if (RecursosP.containsKey(recursotable.getKey()))
		        {
		        	
		        	href.setValue("shared/launchpage.html?content="+RecursosP.get(recursotable.getKey()).getClavilenoid());
		        	
		        	{
					    Attr Atr = document.createAttribute("adlcp:scormType");
					    Atr.setValue("sco");
					    ResourceUni.setAttributeNode(Atr);
					    }
		        	
		        	
		        	
		        	 Element dependencyUni = document.createElement("dependency"); 
				        ResourceUni.appendChild(dependencyUni);
				        
				        {
					        Attr Atr = document.createAttribute("identifierref");
					        Atr.setValue("common_files");
					        dependencyUni.setAttributeNode(Atr);
					        }
		        }
		        
		}
		
		Element ResourceUniA = document.createElement("resource"); 
		resources.appendChild(ResourceUniA);
		
		{
	        Attr Atr = document.createAttribute("identifier");
	        Atr.setValue("assessment_resource");
	        ResourceUniA.setAttributeNode(Atr);
	        }
	        
	        {
		    Attr Atr = document.createAttribute("type");
		    Atr.setValue("webcontent");
		    ResourceUniA.setAttributeNode(Atr);
		    }
	        
	    	{
		        Attr Atr = document.createAttribute("adlcp:scormType");
		        Atr.setValue("sco");
		        ResourceUniA.setAttributeNode(Atr);
		        }
	    	
	    	{
		        Attr Atr = document.createAttribute("href");
		        Atr.setValue("shared/launchpage.html");
		        ResourceUniA.setAttributeNode(Atr);
		        }
		        
	    	Element dependen = document.createElement("dependency"); 
	    	ResourceUniA.appendChild(dependen);
	    	{
		        Attr Atr = document.createAttribute("identifierref");
		        Atr.setValue("common_files");
		        dependen.setAttributeNode(Atr);
		        }
		
		
		Element ResourceUni = document.createElement("resource"); 
		resources.appendChild(ResourceUni);
		
		{
	        Attr Atr = document.createAttribute("identifier");
	        Atr.setValue("common_files");
	        ResourceUni.setAttributeNode(Atr);
	        }
	        
	        {
		    Attr Atr = document.createAttribute("type");
		    Atr.setValue("webcontent");
		    ResourceUni.setAttributeNode(Atr);
		    }
	        
	    	{
		        Attr Atr = document.createAttribute("adlcp:scormType");
		        Atr.setValue("asset");
		        ResourceUni.setAttributeNode(Atr);
		        }
	    	
	    	
	    	String[] files={"shared/assessmenttemplate.html","shared/background.jpg","shared/cclicense.png","shared/contentfunctions.js","shared/launchpage.html","shared/scormfunctions.js","shared/style.css","styleC.css"};
	    	
	    	for (String string : files) {
	    		Element FileUni = document.createElement("file"); 
		        ResourceUni.appendChild(FileUni);
		        
		        {
			        Attr Atr = document.createAttribute("href");
			        Atr.setValue(string);
			        FileUni.setAttributeNode(Atr);
			        }
			}
	    	 
		        
		
	}

	private String processOrganization(
			LinkedList<CompleteDocuments> completeDocumentsList,
			Document document,
			ArrayList<CompleteGrammar> GramaticasAProcesar) {

		String NameBlock = "MAIN_TOC" + completeDocumentsList.get(0).getClavilenoid();

		Element Organization = document.createElement("organization");
		organizations.appendChild(Organization);

		{
			Attr Atr = document.createAttribute("identifier");
			Atr.setValue(NameBlock);
			Organization.setAttributeNode(Atr);
		}

		{
			Attr Atr = document.createAttribute("adlseq:objectivesGlobalToSystem");
			Atr.setValue("false");
			Organization.setAttributeNode(Atr);
		}

		Element Title = document.createElement("title");
		Organization.appendChild(Title);
		Text nodeKeyValue = document.createTextNode(TextoEntrada);
		Title.appendChild(nodeKeyValue);

		Element ItemList = document.createElement("item");
		Organization.appendChild(ItemList);

		Element TitleIL = document.createElement("title");
		ItemList.appendChild(TitleIL);

		Text nodeKeyValueTiL = document.createTextNode("Cases");
		TitleIL.appendChild(nodeKeyValueTiL);

		{
			Attr Atr = document.createAttribute("identifier");
			Atr.setValue("content_wrapper");
			ItemList.setAttributeNode(Atr);
		}

		{
			Attr Atr = document.createAttribute("isvisible");
			Atr.setValue("false");
			ItemList.setAttributeNode(Atr);
		}

//		Gram_doc = new HashMap<CompleteGrammar, HashSet<CompleteDocuments>>();

		while (!completeDocumentsList.isEmpty()) {
			CompleteDocuments completeDocuments = completeDocumentsList.removeLast();

			
			
			
			if (!ProcesadosGeneral.contains(completeDocuments)) {
				ProcesadosGeneral.add(completeDocuments);
				// }
				// for (CompleteDocuments completeDocuments :
				// completeDocumentsList) {

				ArrayList<CompleteGrammar> GramaticasAplicadas = new ArrayList<CompleteGrammar>();

				for (CompleteGrammar completeGrammar : GramaticasAProcesar) {
					if (StaticFunctionsSCORM.isInGrammar(completeDocuments, completeGrammar))
						GramaticasAplicadas.add(completeGrammar);

				}
		     
//				for (CompleteGrammar completeGrammar : GramaticasAplicadas) {
//			    	 
//			    	 
//			    	 HashSet<CompleteDocuments> DocGram=Gram_doc.get(completeGrammar);
//			    	 
//			    	 if (DocGram==null)
//			    		 DocGram=new HashSet<CompleteDocuments>();
//			    	 
//			    	 DocGram.add(completeDocuments);
//			    	 
//			    	 Gram_doc.put(completeGrammar, DocGram);
//			    	 
//			     } 
			if (!GramaticasAplicadas.isEmpty())
				
			{	
				
		     String Grammarname="ID";
	    	 if (GramaticasAplicadas.size()>0)
	    		 Grammarname=GramaticasAplicadas.get(0).getNombre();
		     
		    	 
		    	 HashSet<CompleteDocuments> ListaLinkeados=new HashSet<CompleteDocuments>();
		    	 HashSet<CompleteDocuments> Procesados = new HashSet<CompleteDocuments>();
		    	 Procesados.add(completeDocuments);
		    	 
		    	 
			     
		    	 
		    	 Element Item = document.createElement("item"); 
		    	 ItemList.appendChild(Item);
			     
			     {
				        Attr Atr = document.createAttribute("identifier");
				        Atr.setValue(Grammarname+"_"+completeDocuments.getClavilenoid()+"_"+contador_IDs++);
				        Item.setAttributeNode(Atr);
				        }
				        
				        {
				      
				        String MAINSTR="MAIN_RESOURCE"+(contadorRec++);	
				        String Recurso=ProcessFileHTML(completeDocuments,GramaticasAplicadas,ListaLinkeados);
					    Attr Atr = document.createAttribute("identifierref");
					    Atr.setValue(MAINSTR);
					    Item.setAttributeNode(Atr);
					    Recursos.put(MAINSTR, Recurso);
					    RecursosP.put(MAINSTR,completeDocuments);
					    
					    

					   
					    }
				        
				        Element TitleI = document.createElement("title"); 
				        Item.appendChild(TitleI);
				        
				        String CuterTiyle=Grammarname+": "+completeDocuments.getDescriptionText();
				        if (CuterTiyle.length()>55)
				        	CuterTiyle=CuterTiyle.substring(0, 50)+"...";
				        
					     Text nodeKeyValueTi = document.createTextNode(CuterTiyle);
					     TitleI.appendChild(nodeKeyValueTi);
					     
					     Element imssssequencing = document.createElement("imsss:sequencing"); 
					        Item.appendChild(imssssequencing);
					        
					        Element imsssdeliveryControls = document.createElement("imsss:deliveryControls"); 
					        imssssequencing.appendChild(imsssdeliveryControls);
					     
					        {
						        Attr Atr = document.createAttribute("completionSetByContent");
						        Atr.setValue("true");
						        imsssdeliveryControls.setAttributeNode(Atr);
						        }
					        
					        {
						        Attr Atr = document.createAttribute("objectiveSetByContent");
						        Atr.setValue("true");
						        imsssdeliveryControls.setAttributeNode(Atr);
						        }
					        
					        
					     
					     for (CompleteDocuments completedocHijo : ListaLinkeados) {
					    	 
								if (!Procesados.contains(completedocHijo))
								{
									Procesados.add(completedocHijo);
									ArrayList<CompleteGrammar> GramaticasAProcesarHijo=ProcesaGramaticas(Salvar.getMetamodelGrammar());
									ArrayList<CompleteGrammar> completeGrammarLHijo=new ArrayList<CompleteGrammar>();
									

									
									
									for (CompleteGrammar completeGrammar2 : GramaticasAProcesarHijo) {
										if (StaticFunctionsSCORM.isInGrammar(completedocHijo,completeGrammar2))
											completeGrammarLHijo.add(completeGrammar2);
									
									
									}
									
									 for (CompleteGrammar gramarApp : completeGrammarLHijo) {
									    	if (IsQuiz(gramarApp.getViews()))
									    		 {
									    		
									    		HashSet<CompleteDocuments> List = RecursosQ.get(completeDocuments);
									    		if (List==null)
									    			List=new HashSet<CompleteDocuments>();
									    		
									    		List.add(completedocHijo);
									    		
									    		RecursosQ.put(completeDocuments, List);
									    		 break;
									    		 }
										}
									
									
									if (!completeGrammarLHijo.isEmpty()&&!IsQuiz(completeGrammarLHijo.get(0).getViews()))
										processItem(Item,completedocHijo,completeGrammarLHijo,document,Procesados);
									
								}
							
					     }		     
					     
			}
		     
		     }
		     
		     }
		     
		Element imssssequencing = document.createElement("imsss:sequencing"); 
   	 ItemList.appendChild(imssssequencing);
		     
   	Element imssscontrolMode = document.createElement("imsss:controlMode"); 
   	imssssequencing.appendChild(imssscontrolMode);
		
   	{
		Attr Atr = document.createAttribute("choice");
		Atr.setValue("true");
		imssscontrolMode.setAttributeNode(Atr);
	}

	{
		Attr Atr = document.createAttribute("flow");
		Atr.setValue("true");
		imssscontrolMode.setAttributeNode(Atr);
	}
   	
	Element imsssrollupRules = document.createElement("imsss:rollupRules"); 
   	imssssequencing.appendChild(imsssrollupRules);
		
   	{
		Attr Atr = document.createAttribute("objectiveMeasureWeight");
		Atr.setValue("0");
		imsssrollupRules.setAttributeNode(Atr);
	}

	{
		Attr Atr = document.createAttribute("rollupObjectiveSatisfied");
		Atr.setValue("false");
		imsssrollupRules.setAttributeNode(Atr);
	}
	
	{
		Attr Atr = document.createAttribute("rollupProgressCompletion");
		Atr.setValue("false");
		imsssrollupRules.setAttributeNode(Atr);
	}
	
	Element imsssrollupRule = document.createElement("imsss:rollupRule"); 
	imsssrollupRules.appendChild(imsssrollupRule);
	{
		Attr Atr = document.createAttribute("childActivitySet");
		Atr.setValue("all");
		imsssrollupRules.setAttributeNode(Atr);
	}
	
	Element imsssrollupConditions = document.createElement("imsss:rollupConditions"); 
	imsssrollupRule.appendChild(imsssrollupConditions);
	
	Element imsssrollupCondition = document.createElement("imsss:rollupCondition"); 
	imsssrollupConditions.appendChild(imsssrollupCondition);
	
	{
		Attr Atr = document.createAttribute("condition");
		Atr.setValue("completed");
		imsssrollupCondition.setAttributeNode(Atr);
	}
	
	Element imsssrollupAction = document.createElement("imsss:rollupAction"); 
	imsssrollupRule.appendChild(imsssrollupAction);
	
	{
		Attr Atr = document.createAttribute("action");
		Atr.setValue("satisfied");
		imsssrollupAction.setAttributeNode(Atr);
	}
	
	
	Element imsssobjectives = document.createElement("imsss:objectives"); 
   	imssssequencing.appendChild(imsssobjectives);
	
   	Element imsssprimaryObjective = document.createElement("imsss:primaryObjective"); 
   	imsssobjectives.appendChild(imsssprimaryObjective);
   	
   	
   	{
		Attr Atr = document.createAttribute("objectiveID");
		Atr.setValue("content_completed");
		imsssobjectives.setAttributeNode(Atr);
	}
   	
   	Element imsssmapInfo = document.createElement("imsss:mapInfo"); 
   	imsssprimaryObjective.appendChild(imsssmapInfo);
   	{
		Attr Atr = document.createAttribute("targetObjectiveID");
		Atr.setValue(IDBase+".content_completed");
		imsssmapInfo.setAttributeNode(Atr);
	}
   	
   	{
		Attr Atr = document.createAttribute("writeSatisfiedStatus");
		Atr.setValue("true");
		imsssmapInfo.setAttributeNode(Atr);
	}
   	
   	
   	if (Quizz!=null)
   	{
   	Element ItemT = document.createElement("item");
	Organization.appendChild(ItemT);
	
	Attr AtrI = document.createAttribute("identifier");
	AtrI.setValue("posttest_item");
	imsssobjectives.setAttributeNode(AtrI);
	
	Element PostTestT = document.createElement("title");
	ItemT.appendChild(PostTestT);
	
	Text nodeKeyValueTi = document.createTextNode("Post Test");
	PostTestT.appendChild(nodeKeyValueTi);
	
	int i=1;
	for (Entry<CompleteDocuments, HashSet<CompleteDocuments>> completeGrammar : RecursosQ.entrySet()) {
		
		Element ItemTH = document.createElement("item");
		ItemT.appendChild(ItemTH);
		
		{
			Attr Atr = document.createAttribute("identifier");
			Atr.setValue("test_"+i);
			ItemTH.setAttributeNode(Atr);
		}
		
		{
			Attr Atr = document.createAttribute("identifierref");
			Atr.setValue("assessment_resource");
			ItemTH.setAttributeNode(Atr);
		}
		
		{
			Attr Atr = document.createAttribute("parameters");
			Atr.setValue("?content=q"+completeGrammar.getKey().getClavilenoid());
			ItemTH.setAttributeNode(Atr);
		}
		
		{
			Attr Atr = document.createAttribute("isvisible");
			Atr.setValue("false");
			ItemTH.setAttributeNode(Atr);
		}
		
		Element PostTestIT = document.createElement("title");
		ItemTH.appendChild(PostTestIT);
		
		Text nodeKeyValueTIi = document.createTextNode("Quiz "+i++);
		PostTestIT.appendChild(nodeKeyValueTIi);
		
		Element imssssequencingI = document.createElement("imsss:sequencing");
		ItemTH.appendChild(imssssequencingI);
		
		{
			Attr Atr = document.createAttribute("IDRef");
			Atr.setValue("test_sequencing_rules");
			imssssequencingI.setAttributeNode(Atr);
		}
		
		Element adlnavpresentation = document.createElement("adlnav:presentation");
		ItemTH.appendChild(adlnavpresentation);
		Element adlnavnavigationInterface = document.createElement("adlnav:navigationInterface");
		adlnavpresentation.appendChild(adlnavnavigationInterface);
		Element adlnavhideLMSUI = document.createElement("adlnav:hideLMSUI");
		adlnavnavigationInterface.appendChild(adlnavhideLMSUI);
		Text adlnavhideLMSUIv = document.createTextNode("suspendAll");
		adlnavhideLMSUI.appendChild(adlnavhideLMSUIv);
		}
	
	
	
	Element imssssequencing2 = document.createElement("imsss:sequencing");
	ItemT.appendChild(imssssequencing2);
	
	Element imssscontrolMode2 = document.createElement("imsss:controlMode");
	imssssequencing2.appendChild(imssscontrolMode2);
	
	{
		Attr Atr = document.createAttribute("choice");
		Atr.setValue("false");
		imssscontrolMode2.setAttributeNode(Atr);
	}
	
	{
		Attr Atr = document.createAttribute("flow");
		Atr.setValue("true");
		imssscontrolMode2.setAttributeNode(Atr);
	}
	
	
	{
	Element imssssequencingRules2 = document.createElement("imsss:sequencingRules");
	imssssequencing2.appendChild(imssssequencingRules2);
	
		
		Element imssspreConditionRule2 = document.createElement("imsss:preConditionRule");
		imssssequencingRules2.appendChild(imssspreConditionRule2);
		
		Element imsssruleConditions2 = document.createElement("imsss:ruleConditions");
		imssspreConditionRule2.appendChild(imsssruleConditions2);
		
		{
			Attr Atr = document.createAttribute("conditionCombination");
			Atr.setValue("any");
			imsssruleConditions2.setAttributeNode(Atr);
		}
	
		

		Element imsssruleCondition2 = document.createElement("imsss:ruleCondition");
		imsssruleConditions2.appendChild(imsssruleCondition2);
		
		{
			Attr Atr = document.createAttribute("referencedObjective");
			Atr.setValue("content_completed");
			imsssruleCondition2.setAttributeNode(Atr);
		}
		{
			Attr Atr = document.createAttribute("operator");
			Atr.setValue("not");
			imsssruleCondition2.setAttributeNode(Atr);
		}
		{
			Attr Atr = document.createAttribute("condition");
			Atr.setValue("satisfied");
			imsssruleCondition2.setAttributeNode(Atr);
		}
		
		Element imsssruleCondition_bis = document.createElement("imsss:ruleCondition");
		imsssruleConditions2.appendChild(imsssruleCondition_bis);
		
		{
			Attr Atr = document.createAttribute("referencedObjective");
			Atr.setValue("content_completed");
			imsssruleCondition_bis.setAttributeNode(Atr);
		}
		{
			Attr Atr = document.createAttribute("operator");
			Atr.setValue("not");
			imsssruleCondition_bis.setAttributeNode(Atr);
		}
		{
			Attr Atr = document.createAttribute("condition");
			Atr.setValue("objectiveStatusKnown");
			imsssruleCondition_bis.setAttributeNode(Atr);
		}
		
		
		Element imsssruleAction2 = document.createElement("imsss:ruleAction");
		imssspreConditionRule2.appendChild(imsssruleAction2);
		
		{
			Attr Atr = document.createAttribute("action");
			Atr.setValue("disabled");
			imsssruleAction2.setAttributeNode(Atr);
		}
		
		
		Element imssspreConditionRule2_2 = document.createElement("imsss:preConditionRule");
		imssssequencingRules2.appendChild(imssspreConditionRule2_2);
		
		Element imsssruleConditions2_2 = document.createElement("imsss:ruleConditions");
		imssspreConditionRule2_2.appendChild(imsssruleConditions2_2);
		
		{
			Attr Atr = document.createAttribute("conditionCombination");
			Atr.setValue("any");
			imsssruleConditions2_2.setAttributeNode(Atr);
		}
	
		

		Element imsssruleCondition2_2 = document.createElement("imsss:ruleCondition");
		imsssruleConditions2_2.appendChild(imsssruleCondition2_2);
		{
			Attr Atr = document.createAttribute("condition");
			Atr.setValue("attemptLimitExceeded");
			imsssruleCondition2_2.setAttributeNode(Atr);
		}
		
		Element imsssruleCondition2_3 = document.createElement("imsss:ruleCondition");
		imsssruleConditions2_2.appendChild(imsssruleCondition2_3);
		{
			Attr Atr = document.createAttribute("condition");
			Atr.setValue("satisfied");
			imsssruleCondition2_3.setAttributeNode(Atr);
		}
		
		
		Element imsssruleAction2_2 = document.createElement("imsss:ruleAction");
		imssspreConditionRule2_2.appendChild(imsssruleAction2_2);
		
		{
			Attr Atr = document.createAttribute("action");
			Atr.setValue("disabled");
			imsssruleAction2_2.setAttributeNode(Atr);
		}
		
		
		//POS CONDICIONES
		Element imssspostConditionRule2 = document.createElement("imsss:postConditionRule");
		imssssequencingRules2.appendChild(imssspostConditionRule2);
		{
			Element imsssruleConditionsAb = document.createElement("imsss:ruleConditions");
			imssspostConditionRule2.appendChild(imsssruleConditionsAb);
			
			{
				Attr Atr = document.createAttribute("conditionCombination");
				Atr.setValue("all");
				imsssruleConditionsAb.setAttributeNode(Atr);
			}
			
			Element imsssruleConditionA = document.createElement("imsss:ruleCondition");
			imsssruleConditionsAb.appendChild(imsssruleConditionA);
			{
				Attr Atr = document.createAttribute("condition");
				Atr.setValue("satisfied");
				imsssruleConditionA.setAttributeNode(Atr);
			}
			
			
			{
				Attr Atr = document.createAttribute("operator");
				Atr.setValue("not");
				imsssruleConditionA.setAttributeNode(Atr);
			}
			
			Element imsssruleConditionB = document.createElement("imsss:ruleCondition");
			imsssruleConditionsAb.appendChild(imsssruleConditionB);
			{
				Attr Atr = document.createAttribute("condition");
				Atr.setValue("attemptLimitExceeded");
				imsssruleConditionB.setAttributeNode(Atr);
			}
			
			{
				Attr Atr = document.createAttribute("operator");
				Atr.setValue("not");
				imsssruleConditionB.setAttributeNode(Atr);
			}
			
			Element imsssruleActionAb = document.createElement("imsss:ruleAction");
			imssspostConditionRule2.appendChild(imsssruleActionAb);
			
			{
				Attr Atr = document.createAttribute("action");
				Atr.setValue("retry");
				imsssruleActionAb.setAttributeNode(Atr);
			}
			
		}
		
		
		Element imssspostConditionRule2_2 = document.createElement("imsss:postConditionRule");
		imssssequencingRules2.appendChild(imssspostConditionRule2_2);
		{
			Element imsssruleConditionsAb = document.createElement("imsss:ruleConditions");
			imssspostConditionRule2_2.appendChild(imsssruleConditionsAb);
			
			{
				Attr Atr = document.createAttribute("conditionCombination");
				Atr.setValue("all");
				imsssruleConditionsAb.setAttributeNode(Atr);
			}
			
			Element imsssruleConditionA = document.createElement("imsss:ruleCondition");
			imsssruleConditionsAb.appendChild(imsssruleConditionA);
			{
				Attr Atr = document.createAttribute("condition");
				Atr.setValue("objectiveStatusKnown");
				imsssruleConditionA.setAttributeNode(Atr);
			}
			
			
			{
				Attr Atr = document.createAttribute("operator");
				Atr.setValue("not");
				imsssruleConditionA.setAttributeNode(Atr);
			}
			
			Element imsssruleConditionB = document.createElement("imsss:ruleCondition");
			imsssruleConditionsAb.appendChild(imsssruleConditionB);
			{
				Attr Atr = document.createAttribute("condition");
				Atr.setValue("attemptLimitExceeded");
				imsssruleConditionB.setAttributeNode(Atr);
			}
			
			{
				Attr Atr = document.createAttribute("operator");
				Atr.setValue("not");
				imsssruleConditionB.setAttributeNode(Atr);
			}
			
			Element imsssruleActionAb = document.createElement("imsss:ruleAction");
			imssspostConditionRule2_2.appendChild(imsssruleActionAb);
			
			{
				Attr Atr = document.createAttribute("action");
				Atr.setValue("retry");
				imsssruleActionAb.setAttributeNode(Atr);
			}
			
		}
		
		Element imssspostConditionRule2_3 = document.createElement("imsss:postConditionRule");
		imssssequencingRules2.appendChild(imssspostConditionRule2_3);
		{
			Element imsssruleConditionsAb = document.createElement("imsss:ruleConditions");
			imssspostConditionRule2_3.appendChild(imsssruleConditionsAb);
			
			{
				Attr Atr = document.createAttribute("conditionCombination");
				Atr.setValue("any");
				imsssruleConditionsAb.setAttributeNode(Atr);
			}
			
			Element imsssruleConditionA = document.createElement("imsss:ruleCondition");
			imsssruleConditionsAb.appendChild(imsssruleConditionA);
			{
				Attr Atr = document.createAttribute("condition");
				Atr.setValue("objectiveStatusKnown");
				imsssruleConditionA.setAttributeNode(Atr);
			}
			
			
	
			
			Element imsssruleConditionB = document.createElement("imsss:ruleCondition");
			imsssruleConditionsAb.appendChild(imsssruleConditionB);
			{
				Attr Atr = document.createAttribute("condition");
				Atr.setValue("attemptLimitExceeded");
				imsssruleConditionB.setAttributeNode(Atr);
			}
			
		
			
			Element imsssruleActionAb = document.createElement("imsss:ruleAction");
			imssspostConditionRule2_3.appendChild(imsssruleActionAb);
			
			{
				Attr Atr = document.createAttribute("action");
				Atr.setValue("exitAll");
				imsssruleActionAb.setAttributeNode(Atr);
			}
			
		}
		
		Element imssslimitConditions2 = document.createElement("imsss:limitConditions");
		imssssequencing2.appendChild(imssslimitConditions2);
		
		{
			Attr Atr = document.createAttribute("attemptLimit");
			Atr.setValue("2");
			imssslimitConditions2.setAttributeNode(Atr);
		}
		
		{
		//rollupRules
		Element imsssrollupRules2 = document.createElement("imsss:rollupRules");
		imssssequencing2.appendChild(imsssrollupRules2);
		
		{
		Element imsssrollupRule2 = document.createElement("imsss:rollupRule");
		imsssrollupRules2.appendChild(imsssrollupRule2);
		
		{
			Attr Atr = document.createAttribute("childActivitySet");
			Atr.setValue("any");
			imsssrollupRule2.setAttributeNode(Atr);
		}
		
		Element imsssrollupConditions2 = document.createElement("imsss:rollupConditions");
		imsssrollupRule2.appendChild(imsssrollupConditions2);
		
		Element imsssrollupCondition2 = document.createElement("imsss:rollupCondition");
		imsssrollupConditions2.appendChild(imsssrollupCondition2);
		
		{
			Attr Atr = document.createAttribute("condition");
			Atr.setValue("satisfied");
			imsssrollupCondition2.setAttributeNode(Atr);
		}
		
		Element imsssrollupAction2 = document.createElement("imsss:rollupAction");
		imsssrollupRule2.appendChild(imsssrollupAction2);
		
		{
			Attr Atr = document.createAttribute("action");
			Atr.setValue("satisfied");
			imsssrollupAction2.setAttributeNode(Atr);
		}
		
		}
		
		
		{
			Element imsssrollupRule2 = document.createElement("imsss:rollupRule");
			imsssrollupRules2.appendChild(imsssrollupRule2);
			
			{
				Attr Atr = document.createAttribute("childActivitySet");
				Atr.setValue("any");
				imsssrollupRule2.setAttributeNode(Atr);
			}
			
			Element imsssrollupConditions2 = document.createElement("imsss:rollupConditions");
			imsssrollupRule2.appendChild(imsssrollupConditions2);
			
			Element imsssrollupCondition2 = document.createElement("imsss:rollupCondition");
			imsssrollupConditions2.appendChild(imsssrollupCondition2);
			
			{
				Attr Atr = document.createAttribute("condition");
				Atr.setValue("satisfied");
				imsssrollupCondition2.setAttributeNode(Atr);
			}
			
			{
				Attr Atr = document.createAttribute("operator");
				Atr.setValue("not");
				imsssrollupCondition2.setAttributeNode(Atr);
			}
			
			Element imsssrollupAction2 = document.createElement("imsss:rollupAction");
			imsssrollupRule2.appendChild(imsssrollupAction2);
			
			{
				Attr Atr = document.createAttribute("action");
				Atr.setValue("notSatisfied");
				imsssrollupAction2.setAttributeNode(Atr);
			}
			
			}
		
		
		
		}
		
		{
			
			
			
			Element imsssobjectives2 = document.createElement("imsss:objectives");
			imssssequencing2.appendChild(imsssobjectives2);
			
			{
			Element imsssprimaryObjective2 = document.createElement("imsss:primaryObjective");
			imsssobjectives2.appendChild(imsssprimaryObjective2);
			{
				Attr Atr = document.createAttribute("objectiveID");
				Atr.setValue("course_score");
				imsssprimaryObjective2.setAttributeNode(Atr);
			}
			
			Element imsssmapInfo2 = document.createElement("imsss:mapInfo");
			imsssprimaryObjective2.appendChild(imsssmapInfo2);
			{
				Attr Atr = document.createAttribute("targetObjectiveID");
				Atr.setValue(IDBase + ".course_score");
				imsssmapInfo2.setAttributeNode(Atr);
			}
			
			{
				Attr Atr = document.createAttribute("readSatisfiedStatus");
				Atr.setValue("false");
				imsssmapInfo2.setAttributeNode(Atr);
			}
			
			{
				Attr Atr = document.createAttribute("readNormalizedMeasure");
				Atr.setValue("true");
				imsssmapInfo2.setAttributeNode(Atr);
			}
			
			}
			
			
			
			{
				Element imsssprimaryObjective2 = document.createElement("imsss:objective");
				imsssobjectives2.appendChild(imsssprimaryObjective2);
				{
					Attr Atr = document.createAttribute("objectiveID");
					Atr.setValue("content_completed");
					imsssprimaryObjective2.setAttributeNode(Atr);
				}
				
				Element imsssmapInfo2 = document.createElement("imsss:mapInfo");
				imsssprimaryObjective2.appendChild(imsssmapInfo2);
				{
					Attr Atr = document.createAttribute("targetObjectiveID");
					Atr.setValue(IDBase + ".content_completed");
					imsssmapInfo2.setAttributeNode(Atr);
				}
				
				{
					Attr Atr = document.createAttribute("readSatisfiedStatus");
					Atr.setValue("false");
					imsssmapInfo2.setAttributeNode(Atr);
				}
				
			}
			
			Element imsssrandomizationControls2 = document.createElement("imsss:randomizationControls");
			imssssequencing2.appendChild(imsssrandomizationControls2);
			
			{
				Attr Atr = document.createAttribute("randomizationTiming");
				Atr.setValue("onEachNewAttempt");
				imsssrandomizationControls2.setAttributeNode(Atr);
			}
			
			{
				Attr Atr = document.createAttribute("reorderChildren");
				Atr.setValue("true");
				imsssrandomizationControls2.setAttributeNode(Atr);
			}
			
			

			
//			primaryObjective
		}
	}
	
	
	
   	}
	
	//sequencing
   	Element imsssequencingOut = document.createElement("imsss:sequencing");
	Organization.appendChild(imsssequencingOut);

	Element imsscontrolModeOut = document.createElement("imsss:controlMode");
	imsssequencingOut.appendChild(imsscontrolModeOut);
   	
	{
		Attr Atr = document.createAttribute("choice");
		Atr.setValue("true");
		imsssequencingOut.setAttributeNode(Atr);
	}
	
	{
		Attr Atr = document.createAttribute("flow");
		Atr.setValue("true");
		imsssequencingOut.setAttributeNode(Atr);
	}
   	
	{
		
		Element imsssrollupRules2 = document.createElement("imsss:rollupRules");
		imsssequencingOut.appendChild(imsssrollupRules2);
		
		//POR AQUI
		
		Element imsssrollupRule2 = document.createElement("imsss:rollupRule");
		imsssrollupRules2.appendChild(imsssrollupRule2);
		
		{
			Attr Atr = document.createAttribute("childActivitySet");
			Atr.setValue("any");
			imsssrollupRule2.setAttributeNode(Atr);
		}
		
		Element imsssrollupConditions2 = document.createElement("imsss:rollupConditions");
		imsssrollupRule2.appendChild(imsssrollupConditions2);
		
		{
			Attr Atr = document.createAttribute("conditionCombination");
			Atr.setValue("any");
			imsssrollupConditions2.setAttributeNode(Atr);
		}
		
		Element imsssrollupCondition2 = document.createElement("imsss:rollupCondition");
		imsssrollupConditions2.appendChild(imsssrollupCondition2);
		
		{
			Attr Atr = document.createAttribute("condition");
			Atr.setValue("satisfied");
			imsssrollupCondition2.setAttributeNode(Atr);
		}
		
		
		Element imsssrollupCondition2_2 = document.createElement("imsss:rollupCondition");
		imsssrollupConditions2.appendChild(imsssrollupCondition2_2);
		
		{
			Attr Atr = document.createAttribute("condition");
			Atr.setValue("attemptLimitExceeded");
			imsssrollupCondition2_2.setAttributeNode(Atr);
		}
		

		
		Element imsssrollupAction2 = document.createElement("imsss:rollupAction");
		imsssrollupRule2.appendChild(imsssrollupAction2);
		
		{
			Attr Atr = document.createAttribute("action");
			Atr.setValue("completed");
			imsssrollupAction2.setAttributeNode(Atr);
		}
		
		}
   	
	//Fuera de QUiz falta un secuencing
	
		return NameBlock;
	}





	private void processItem(Element item, CompleteDocuments completeDocuments,
			ArrayList<CompleteGrammar> GramaticasAplicadas, Document document, HashSet<CompleteDocuments> procesados) {
		
	    	 
	    	 HashSet<CompleteDocuments> ListaLinkeados=new HashSet<CompleteDocuments>();
	    	 HashSet<CompleteDocuments> Procesados=new HashSet<CompleteDocuments>(procesados);
	    	 
	    	 
	    	 
	    	 
//	    	 for (CompleteGrammar completeGrammar : GramaticasAplicadas) {
//	    	 
//	    	 HashSet<CompleteDocuments> DocGram=Gram_doc.get(completeGrammar);
//	    	 
//	    	 if (DocGram==null)
//	    		 DocGram=new HashSet<CompleteDocuments>();
//	    	 
//	    	 DocGram.add(completeDocuments);
//	    	 
//	    	 Gram_doc.put(completeGrammar, DocGram);
//	    	 
//	    	 }
	    	 
	    	 String Grammarname="ungrammar";
	    	 if (GramaticasAplicadas.size()>0)
	    		 Grammarname=GramaticasAplicadas.get(0).getNombre();
	    	 
	    	 
	    	 
	    	 Element Item = document.createElement("item"); 
	    	 item.appendChild(Item);
		     
		     {
			        Attr Atr = document.createAttribute("identifier");
			        Atr.setValue(Grammarname +": " +completeDocuments.getClavilenoid()+"_"+contador_IDs++);
			        Item.setAttributeNode(Atr);
			        }
			        
			        {
			      
			        String MAINSTR="MAIN_RESOURCE"+(contadorRec++);	
			        String Recurso=ProcessFileHTML(completeDocuments,GramaticasAplicadas,ListaLinkeados);
				    Attr Atr = document.createAttribute("identifierref");
				    Atr.setValue(MAINSTR);
				    Item.setAttributeNode(Atr);
				    Recursos.put(MAINSTR, Recurso);
				    RecursosP.put(MAINSTR,completeDocuments);
				    
				    
//				    for (CompleteGrammar gramarApp : GramaticasAplicadas) {
//				    	if (IsQuiz(gramarApp.getViews()))
//				    		 {
//				    		RecursosQ.put(completeDocuments, Recurso);
//				    		 break;
//				    		 }
//					}
				    
				    }
			        
			        Element TitleI = document.createElement("title"); 
			        Item.appendChild(TitleI);
			        
			        String CuterTiyle=Grammarname+": "+completeDocuments.getDescriptionText();
			        if (CuterTiyle.length()>55)
			        	CuterTiyle=CuterTiyle.substring(0, 50)+"...";
			        
				     Text nodeKeyValueTi = document.createTextNode(CuterTiyle);
				     TitleI.appendChild(nodeKeyValueTi);
				     
				     
				     for (CompleteDocuments completedocHijo : ListaLinkeados) {
				    	 
				    	 
							if (!Procesados.contains(completedocHijo))
							{
								Procesados.add(completedocHijo);
								ArrayList<CompleteGrammar> GramaticasAProcesarHijo=ProcesaGramaticas(Salvar.getMetamodelGrammar());
								ArrayList<CompleteGrammar> completeGrammarLHijo=new ArrayList<CompleteGrammar>();
								

								
								
								for (CompleteGrammar completeGrammar2 : GramaticasAProcesarHijo) {
									if (StaticFunctionsSCORM.isInGrammar(completedocHijo,completeGrammar2))
										completeGrammarLHijo.add(completeGrammar2);
								
								
								}
								
								
//								 for (CompleteGrammar gramarApp : completeGrammarLHijo) {
//								    	if (IsQuiz(gramarApp.getViews()))
//								    		 {
//								    		
//								    		HashSet<CompleteDocuments> List = RecursosQ.get(completeDocuments);
//								    		if (List==null)
//								    			List=new HashSet<CompleteDocuments>();
//								    		
//								    		List.add(completedocHijo);
//								    		
//								    		RecursosQ.put(completeDocuments, List);
//								    		 break;
//								    		 }
//									}
						    	
								
								if (!completeGrammarLHijo.isEmpty()&&!IsQuiz(completeGrammarLHijo.get(0).getViews()))
									 processItem(Item,completedocHijo,completeGrammarLHijo,document,Procesados);
								
							}
						
				     
				     
		}
		
	}

	private String ProcessFileHTML(CompleteDocuments completeDocuments,List<CompleteGrammar> completeGrammarL, HashSet<CompleteDocuments> listaLinkeados) {
		
		String SalidaWeb=TablaHTML.get(completeDocuments.getClavilenoid());
		if (SalidaWeb!=null)
			{
			listaLinkeados.addAll(TablaHTMLLink.get(completeDocuments.getClavilenoid()));
			return SalidaWeb;
			}
		

		
		

//		List<CompleteElement> listaElem = completeDocuments.getDescription();
//		List<Long> DocumentosHijos=procesaElement(listaElem);

		
		
			StringBuffer CodigoHTML = new StringBuffer();
			CodigoHTML.append("<html>");
			CodigoHTML.append("<head>");  
			CodigoHTML.append("<title>"+EXPORTTEXT+"</title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"); 
			CodigoHTML.append("<link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"styleC.css\">");
			CodigoHTML.append("<meta name=\"description\" content=\"Informe generado por el sistema "+CLAVY+"\">");
			Calendar C=new GregorianCalendar();
			DateFormat df = new SimpleDateFormat ("yyyy-MM-dd");
			String ValueHoy = df.format(C.getTime());	
			CodigoHTML.append("<meta name=\"fecha\" content=\""+ValueHoy+"\">");
			CodigoHTML.append("<meta name=\"author\" content=\"Grupo de investigación ILSA-UCM\">");
//			CodigoHTML.append("<style>");
//			CodigoHTML.append("li.doc {color: blue;}");	
//			CodigoHTML.append("</style>");
			CodigoHTML.append("<link href=\"lightbox/css/lightbox.css\" rel=\"stylesheet\" />");
			CodigoHTML.append("<script src=\"lightbox/js/lightbox-plus-jquery.js\"></script>"); 
			CodigoHTML.append("<script type=\"text/javascript\" src=\"https://maps.googleapis.com/maps/api/js?key=AIzaSyBPj8iz7libsW74GvKYCU7VdtggaOA8814&amp;v=3&sensor=true&libraries=places\"></script> \n");
			CodigoHTML.append("<script src=\"http://maps.google.com/maps/api/js\"></script> \n"+
  "<script src=\"gmaps.js\"></script> \n"+
  "<style type=\"text/css\"> \n"+
  " .map { \n"+
  "    width: 100% !important; \n"+
  "    height: 400px !important; \n"+
  "  } \n"+
  "</style>");
			
			CodigoHTML.append("</head> \n"); 
			
			CodigoHTML.append("<body onload=\"primeraTab()\">");
			
			CodigoHTML.append("<script> \n");
			CodigoHTML.append("function openCity(evt, cityName) {"+
"    var i, tabcontent, tablinks; " +
"    tabcontent = document.getElementsByClassName(\"tabcontent\");"+
"    for (i = 0; i < tabcontent.length; i++) {"+
"        tabcontent[i].style.display = \"none\";"+
"    }"+
"    tablinks = document.getElementsByClassName(\"tablinks\");"+
"    for (i = 0; i < tablinks.length; i++) {"+
"        tablinks[i].className = tablinks[i].className.replace(\" active\", \"\");"+
"    }"+
"    document.getElementById(cityName).style.display = \"block\";"+
"    evt.currentTarget.className += \" active\";"+
"map.refresh();"+
"} \n");
			
			CodigoHTML.append("function primeraTab() {");
					CodigoHTML.append(" openCity(event, 'Document');");
							CodigoHTML.append("}");
							
		CodigoHTML.append("</script>");
	
		HashMap<StringBuffer,String> Pesatanas=new HashMap<StringBuffer,String>();
		ArrayList<StringBuffer> ListaPestanas=new ArrayList<StringBuffer>();
		HashSet<String> ListaNomPestanas=new HashSet<String>();
		
		StringBuffer Document=new StringBuffer();
		
		Document.append("	<div id=\"Document\" class=\"tabcontent\">");
		Pesatanas.put(Document, "Document");
		ListaPestanas.add(Document);
		ListaNomPestanas.add("Document");
				
			Document.append("<ul class=\"_List General\">");
			
			File IconF=new File(SOURCE_FOLDER+File.separator+completeDocuments.getClavilenoid());
			IconF.mkdirs();
			
			
			String Path=StaticFunctionsSCORM.calculaIconoString(completeDocuments.getIcon());
			
			String[] spliteStri=Path.split("/");
			String NameS = spliteStri[spliteStri.length-1];
			String Icon=SOURCE_FOLDER+File.separator+completeDocuments.getClavilenoid()+File.separator+NameS;
			
			try {
			
			
			
			
			
			NameS=URLEncoder.encode(NameS, "UTF-8");
			
				URL url2 = new URL(Path);
//				url2=parseusrl(url2);
//				CL.getLogLines().add("Image copy, file with url ->>"+url2.toString()+"");
				saveImage(url2, Icon);
			} catch (Exception e) {
				e.printStackTrace();
				CL.getLogLines().add("Error in Icon copy, file with url ->>"+completeDocuments.getIcon()+" not found or restringed");
				
			}
			
			int width= 50;
			int height=50;
			int widthmini= 50;
			int heightmini=50;
			
			try {
				BufferedImage bimg = ImageIO.read(new File(Icon));
				width= bimg.getWidth();
				height= bimg.getHeight();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			 widthmini= 50;
			 heightmini= (50*height)/width;

			Document.append("<li> <span class=\"_Type Icon N_1\">Icon:</span> <a data-lightbox=\"image_"+counter+++"\"  href=\""+completeDocuments.getClavilenoid()+File.separator+NameS+"\" ><img class=\"Icon _Value N_1V\" src=\""+
			completeDocuments.getClavilenoid()+File.separator+NameS+"\" "
//					+ "onmouseover=\"this.width="+width+";this.height="+height+";\" onmouseout=\"this.width="+widthmini+";this.height="+heightmini+";\""
							+ " width=\""+widthmini+"\" height=\""+heightmini+"\" alt=\""+Path+"\" /></a></li>");
			Document.append("<li> <span class=\"_Type Description N_2\">Description:</span> <span class=\"Description _Value N_0V\">"+textToHtmlConvertingURLsToLinks(completeDocuments.getDescriptionText())+"</span></li>");
		
			
			for (CompleteGrammar completeGrammar : completeGrammarL) {

				StringBuffer GrammarBuf=new StringBuffer();
				
				String StringName=completeGrammar.getNombre();
				int indi=0;
				while (ListaNomPestanas.contains(StringName))
				{
					StringName=completeGrammar.getNombre()+indi;
					indi++;
				}
				
				GrammarBuf.append("	<div id=\""+StringName+"\" class=\"tabcontent\">");
				Pesatanas.put(GrammarBuf, StringName);
				ListaPestanas.add(GrammarBuf);
				ListaNomPestanas.add(StringName);
				
				HashMap<Long,StringBuffer> Ignore=new HashMap<Long,StringBuffer>();
				ArrayList<StringBuffer> openednoClose=new ArrayList<StringBuffer>();
				
				for (CompleteElementType completeST : completeGrammar.getSons()) {
					
					if (StaticFunctionsSCORM.isVisible(completeST)&&!StaticFunctionsSCORM.isQOptions(completeST)&&!StaticFunctionsSCORM.isQAnswer(completeST))
					{
					
					if (StaticFunctionsSCORM.isMap(completeST)&&StaticFunctionsSCORM.hasValuedChildren(completeST,completeDocuments.getDescription()))	
					{
						
						Double Lat;
						Double Long;
						
						Lat=StaticFunctionsSCORM.getLat(completeST.getSons(),completeDocuments);
						Long=StaticFunctionsSCORM.getLong(completeST.getSons(),completeDocuments);
						
						if (Lat!=null&&Long!=null)
						
						{
						String mapOrderString="map"+nummap++;
						//CASO MAPAS
						GrammarBuf.append("	<div id=\""+mapOrderString+"\" class=\"map\">");
						GrammarBuf.append("	</div>");
						GrammarBuf.append("<script> \n"+
					    "var map = new GMaps({ \n"+
					    "el: '#"+mapOrderString+"', \n"+
					    "lat: "+Lat+", \n"+
					    "lng: "+Long+", \n"+
					    "zoom: 10 \n"+
						"}); \n");
						
						StringBuffer StructBufH = new StringBuffer();
						
						StructBufH.append("	<div id=\""+mapOrderString+"\" class=\"hijomap\">");
						
						for (CompleteElementType CETY : completeST.getSons()) {
							if (!StaticFunctionsSCORM.isLatitude(CETY)&&!StaticFunctionsSCORM.isLongitude(CETY))
							{
								String Salida = processST(CETY,completeDocuments,listaLinkeados);
								if (!Salida.isEmpty())
									StructBufH.append(Salida);
								
							}
								
						}
						
						StructBufH.append("	</div>");
						
						
						GrammarBuf.append("map.addMarker({ \n"+
						"lat: "+Lat+", \n"+
						"lng: "+Long+", \n"+
						"infoWindow: { \n"+
						"	  content: '"+StructBufH.toString()+"'\n"+
						"	}\n"+
						"});");
						
						GrammarBuf.append("</script>");
						
						}
					}
					else if (!Valorable(completeST))
					{
						
						if (!Ignore.containsKey(completeST.getClassOfIterator().getClavilenoid()))
						{
						
							
							
						StringBuffer StructBuf=new StringBuffer();
						
						
						
						Ignore.put(completeST.getClassOfIterator().getClavilenoid(),StructBuf );
						
						String StringNameInt=completeST.getName();
						int indiInt=0;
						while (ListaNomPestanas.contains(StringNameInt))
						{
							StringNameInt=completeST.getName()+indiInt;
							indiInt++;
						}
						
						StructBuf.append("	<div id=\""+StringNameInt+"\" class=\"tabcontent\">");
						Pesatanas.put(StructBuf, StringNameInt);
						ListaPestanas.add(StructBuf);
						ListaNomPestanas.add(StringName);
						
						String Salida = processST(completeST,completeDocuments,listaLinkeados);
						if (!Salida.isEmpty())
							StructBuf.append(Salida);
						
						openednoClose.add(StructBuf);
						
						}
						else
						{
							StringBuffer StructBuf = Ignore.get(completeST.getClassOfIterator().getClavilenoid());
							String Salida = processST(completeST,completeDocuments,listaLinkeados);
							if (!Salida.isEmpty())
								StructBuf.append(Salida);
						}
						
					}
					else
					{
					
					String Salida = processST(completeST,completeDocuments,listaLinkeados);
					if (!Salida.isEmpty())
						GrammarBuf.append(Salida);
					}
					
					}
				}
			
				for (StringBuffer stringBuffer : openednoClose) {
					stringBuffer.append("	</div>");
				}
				GrammarBuf.append("	</div>");
		}
			
			
			Document.append("	</div>");
			
			
			CodigoHTML.append("	<div class=\"tab\">");
			
			boolean primero=true;
			for (StringBuffer stringB : ListaPestanas) {
				String Name = Pesatanas.get(stringB);
				
				String Extra = "";
				if (primero)
					{
					Extra=" active";
					primero=false;
					}
				
				CodigoHTML.append("  <button class=\"tablinks"+Extra+"\" onclick=\"openCity(event, '"+Name+"')\">"+Name+"</button>");

			}
			
			CodigoHTML.append("	</div>");
		
			
			

		
			
			
			for (StringBuffer stringB : ListaPestanas) 
				CodigoHTML.append(stringB.toString());
			//AQUI METER TODO
			
			
			CodigoHTML.append("</body>");
			CodigoHTML.append("</html>");
			
			
			
			
			//Else
			SalidaWeb=creaLaWeb(CodigoHTML,Long.toString(completeDocuments.getClavilenoid()));
			TablaHTML.put(completeDocuments.getClavilenoid(), SalidaWeb);
			TablaHTMLLink.put(completeDocuments.getClavilenoid(), listaLinkeados);
			
		
		return SalidaWeb;
			
//			return creaLaWeb(CodigoHTML,Long.toString(completeDocuments.getClavilenoid()));
	}

//	private URL parseusrl(URL url2) throws URISyntaxException, MalformedURLException {
//		//TODO ESTO SE QUITO PERO NO SE PORQUE SE PUSO
////		 CL.getLogLines().add("Image copy, file with url ->>"+url2.toString()+"");
////		 URI uri2 = new URI(url2.getProtocol(), url2.getUserInfo(), url2.getHost(), url2.getPort(), url2.getPath(), url2.getQuery(), url2.getRef());
////		 return uri2.toURL();
//		 
//		 return url2;
//
//	}

	private void processMetadata(Document document) {
		{
			Element keyNode = document.createElement("schema"); 
            Text nodeKeyValue = document.createTextNode("ADL SCORM");	
            keyNode.appendChild(nodeKeyValue);
            metadata.appendChild(keyNode);
		}
		
		{
			Element keyNode = document.createElement("schemaversion"); 
            Text nodeKeyValue = document.createTextNode("2004 3rd Edition");	
            keyNode.appendChild(nodeKeyValue);
            metadata.appendChild(keyNode);
		}
		
		
//		//IMS LOM
//		{
//			Element LOM = document.createElement("imsmd:lom"); 
//            metadata.appendChild(LOM);
//            
//            Element General = document.createElement("imsmd:general"); 
//            LOM.appendChild(General);
//            
//            Element Title = document.createElement("imsmd:title"); 
//            General.appendChild(Title);
//            
//            Element Lan = document.createElement("imsmd:langstring"); 
//            Title.appendChild(Lan);
//            
//            Attr Atr = document.createAttribute("xml:lang");
//			Atr.setValue("en-US");
//			Lan.setAttributeNode(Atr);
//			
//			Text nodeKeyValue = document.createTextNode(TextoEntrada);
//			Lan.appendChild(nodeKeyValue);
//		}
		
		
		
	}

	protected void quicksort(ArrayList<CompleteDocuments> A, int izq, int der) {

		  CompleteDocuments pivote=A.get(izq); // tomamos primer elemento como pivote
		  int i=izq; // i realiza la búsqueda de izquierda a derecha
		  int j=der; // j realiza la búsqueda de derecha a izquierda
		  CompleteDocuments aux;
		 
		  while(i<j){            // mientras no se crucen las búsquedas
		     while(A.get(i).getClavilenoid()<=pivote.getClavilenoid() && i<j) i++; // busca elemento mayor que pivote
		     while(A.get(j).getClavilenoid()>pivote.getClavilenoid()) j--;         // busca elemento menor que pivote
		     if (i<j) {                      // si no se han cruzado                      
		         aux= A.get(i);                  // los intercambia
		         A.set(i, A.get(j));
		         A.set(j,aux);
		     }
		   }
		  A.set(izq,A.get(j)); // se coloca el pivote en su lugar de forma que tendremos
		  A.set(j,pivote); // los menores a su izquierda y los mayores a su derecha
		   if(izq<j-1)
		      quicksort(A,izq,j-1); // ordenamos subarray izquierdo
		   if(j+1 <der)
		      quicksort(A,j+1,der); // ordenamos subarray derecho
		}

	private void creaLACSS() {
		 FileWriter filewriter = null;
		 PrintWriter printw = null;
		    
		try {
			 filewriter = new FileWriter(SOURCE_FOLDER+"\\styleC.css");//declarar el archivo
		     printw = new PrintWriter(filewriter);//declarar un impresor
		          
		     printw.println("li._Document {color: blue;}");
		     printw.println("span._Type {font-weight: bold;}");
		     printw.println("ul._List {}");
		     printw.println("span._Value {}");
		     printw.println(".tab {overflow: hidden; border: 1px solid #ccc; background-color: #f1f1f1;}");
		     printw.println(".tab button { background-color: inherit; float: left; border: none; outline: none; cursor: pointer; padding: 14px 16px; transition: 0.3s; }");
		     printw.println(".tab button:hover {background-color: #ddd;}");
		     printw.println(".tab button.active {background-color: #ccc;}");
		     printw.println(".tabcontent {display: none; padding: 6px 12px; border: 1px solid #ccc; border-top: none;}");
		     
		     printw.close();//cerramos el archivo
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeErrorException(new Error(e), "Error de archivo");
		} 
		
	}

	private String creaLaWeb(StringBuffer CodigoHTML,String LongName) {
//		 FileWriter filewriter = null;
//		 PrintWriter printw = null;
		    

		
		try {
			String Salida=LongName+".html";
			File fileDir = new File(SOURCE_FOLDER+"\\"+Salida);
			 
			Writer out = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(fileDir), "UTF8"));

			
//			String html = CodigoHTML.toString()
//			org.jsoup.nodes.Document doc = Jsoup.parseBodyFragment(html);
//			String SalidaHTML=doc.body().html();
			
			out.append(CodigoHTML.toString());
	 
			out.flush();
			out.close();
			
			return Salida;
//			 filewriter = new FileWriter(SOURCE_FOLDER+"\\index.html");//declarar el archivo
//		     printw = new PrintWriter(filewriter);//declarar un impresor
//		          
//		     printw.println(CodigoHTML.toString());
//		     
//		     printw.close();//cerramos el archivo
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeErrorException(new Error(e), "Error de archivo");
		} 
		 
		            

		
	}

	/**
	 * Salva una imagen dado un destino
	 * @param imageUrl
	 * @param destinationFile
	 * @throws IOException
	 */
	protected void saveImage(URL imageUrl, String destinationFile) throws IOException {

//		URL url = imageUrl;
//		InputStream is = url.openStream();
//		OutputStream os = new FileOutputStream(destinationFile);
//
//		byte[] b = new byte[2048];
//		int length;
//
//		while ((length = is.read(b)) != -1) {
//			os.write(b, 0, length);
//		}
//
//		is.close();
//		os.close();
		
		
		
		// This will get input data from the server
	    InputStream inputStream = null;

	    // This will read the data from the server;
	    OutputStream outputStream = null;

//	        // This will open a socket from client to server
//	        URL url = new URL(search);

	       // This user agent is for if the server wants real humans to visit
	        String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";

	       // This socket type will allow to set user_agent
	        URLConnection con = imageUrl.openConnection();

	        // Setting the user agent
	        con.setRequestProperty("User-Agent", USER_AGENT);

	        // Requesting input data from server
	        inputStream = con.getInputStream();

	        // Open local file writer
	        outputStream = new FileOutputStream(destinationFile);

	        // Limiting byte written to file per loop
	        byte[] buffer = new byte[2048];

	        // Increments file size
	        int length;

	        // Looping until server finishes
	        while ((length = inputStream.read(buffer)) != -1) {
	            // Writing data
	            outputStream.write(buffer, 0, length);
	        }

	     // closing used resources
	     // The computer will not be able to use the image
	     // This is a must

	     outputStream.close();
	     inputStream.close();
		
	}

	private String processST(CompleteElementType completeST,
			CompleteDocuments completeDocuments, HashSet<CompleteDocuments> listaLinkeados) {
		
		if (!StaticFunctionsSCORM.isVisible(completeST))
			return "";
		
		StringBuffer StringSalida=new StringBuffer();
//		StringBuffer Pestanadentro=new StringBuffer();
		boolean Vacio=true;
//		boolean ProcesaAparte=false;
			CompleteElement E=findElem(completeST,completeDocuments.getDescription());
			
			
			String tipo = ReduceString(((CompleteElementType)completeST).getName());
			
			if (NameCSS.get(tipo)!=null&&NameCSS.get(tipo)!=completeST)
				tipo=CreateNameCSS(tipo,completeST);
			
			NameCSS.put(tipo,completeST);
			 
			String IDT=((CompleteElementType)completeST).getClavilenoid()+"";
			
			tipo=tipo+" N"+IDT;
			
			
			
			if (E!=null)
				{
				Vacio=false;
				if (E instanceof CompleteTextElement)
					{
					StringSalida.append("<li> <span class=\"_Type "+tipo+"\">"+((CompleteElementType)completeST).getName()+":</span> <span class=\""+tipo+"V"+" _Value\">"+textToHtmlConvertingURLsToLinks(((CompleteTextElement)E).getValue())+"</span> </li>");
					}
				else if (E instanceof CompleteLinkElement)
					{
					CompleteDocuments Linked=((CompleteLinkElement) E).getValue();
					
					
					listaLinkeados.add(Linked);
					
					File IconF=new File(SOURCE_FOLDER+File.separator+completeDocuments.getClavilenoid());
					IconF.mkdirs();
					
					if (Linked!=null)
					{
						
					String Path=StaticFunctionsSCORM.calculaIconoString(Linked.getIcon());
					
					
					String[] spliteStri=Path.split("/");
					String NameS = spliteStri[spliteStri.length-1];
					String Icon=SOURCE_FOLDER+File.separator+completeDocuments.getClavilenoid()+File.separator+NameS;
					
					try {
					
					NameS=URLEncoder.encode(NameS, "UTF-8");
						URL url2 = new URL(Path);
//						url2=parseusrl(url2);
//						CL.getLogLines().add("Image copy, file with url ->>"+url2.toString()+"");
						saveImage(url2, Icon);
					} catch (Exception e) {
						e.printStackTrace();
						CL.getLogLines().add("Error in Image copy, file with url ->>"+Linked.getIcon()+" not found or restringed");
					}
					
					
					
					int width= 50;
					int height=50;
					int widthmini= 50;
					int heightmini=50;
					
					try {
						BufferedImage bimg = ImageIO.read(new File(Icon));
						width= bimg.getWidth();
						height= bimg.getHeight();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					
					 widthmini= 50;
					 heightmini= (50*height)/width;
					
					
					StringSalida.append("<li> <span class=\"_Type "+tipo+"\">"+((CompleteElementType)completeST).getName()+":</span> <a data-lightbox=\"image_"+counter+++"\"  href=\""+completeDocuments.getClavilenoid()+File.separator+NameS+"\" ><img class=\"_ImagenOV "+tipo+"V \" src=\""+
							completeDocuments.getClavilenoid()+File.separator+NameS+"\""
//									+ " onmouseover=\"this.width="+width+";this.height="+height+";\" onmouseout=\"this.width="+widthmini+";this.height="+heightmini+";\""
											+ " width=\""+widthmini+"\" height=\""+heightmini+"\" alt=\""+completeDocuments.getClavilenoid()+File.separator+NameS+"\" /> </a>"+
//							"<span class=\""+tipo+"V _ClavyID _Value\">" +Linked.getClavilenoid()+"</span>"+
							"<span class=\""+tipo+"V _DescriptionRel _Value\">" +textToHtmlConvertingURLsToLinks(Linked.getDescriptionText())+"</span></li>");
					
					
					}
					}
				else if (E instanceof CompleteResourceElementURL)
					{
					String Link = ((CompleteResourceElementURL)E).getValue();
							
					
					if (StaticFunctionsSCORM.isimage(Link.toLowerCase()))
					{
						String Path=StaticFunctionsSCORM.calculaIconoString(Link);
						
						
						String[] spliteStri=Path.split("/");
						String NameS = spliteStri[spliteStri.length-1];
						String Icon=SOURCE_FOLDER+File.separator+completeDocuments.getClavilenoid()+File.separator+NameS;
						
						try {
						
						
						NameS=URLEncoder.encode(NameS, "UTF-8");
						
						
						File test=new File(Icon);
						while (test.exists())
							{
							NameS = "rep_"+(contadorFiles++)+spliteStri[spliteStri.length-1];
							Icon=SOURCE_FOLDER+File.separator+completeDocuments.getClavilenoid()+File.separator+NameS;
							
							NameS=URLEncoder.encode(NameS, "UTF-8");
							
						
							
							test=new File(Icon);
							}
						

							URL url2 = new URL(Path);
//							url2=parseusrl(url2);
//							CL.getLogLines().add("Image copy, file with url ->>"+url2.toString()+"");
							saveImage(url2, Icon);
						} catch (Exception e) {
							e.printStackTrace();
							CL.getLogLines().add("Error in Image copy, file with url ->> "+Link+" not found or restringed");
						}
						
						int width= 50;
						int height=50;
						int widthmini= 50;
						int heightmini=50;
						
						try {
							BufferedImage bimg = ImageIO.read(new File(Icon));
							width= bimg.getWidth();
							height= bimg.getHeight();
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						
						 widthmini= 50;
						 heightmini= (50*height)/width;
						
						 StringSalida.append("<li> <span class=\"_Type "+tipo+"\">"+((CompleteElementType)completeST).getName()+":</span> " +
//									"File Linked ->"+
									"<a data-lightbox=\"image_"+counter+++"\" class=\"_LinkedRef "+tipo+"V "+tipo+"A  \" href=\""+completeDocuments.getClavilenoid()+File.separator+NameS+"\" target=\"_blank\">"+
									" <img class=\"_ImagenFile "+tipo+"V \" class=\"ImagenOV\" src=\""+completeDocuments.getClavilenoid()+File.separator+NameS+"\"" +
//											" onmouseover=\"this.width="+width+";this.height="+height+";\" onmouseout=\"this.width="+widthmini+";this.height="+heightmini+";\"" +
													 " width=\""+widthmini+"\" height=\""+heightmini+"\" alt=\""+completeDocuments.getClavilenoid()+File.separator+NameS+"\" />" +
											"</a></li>");	
					
					}
					else
					{
					
							if (!testLink(Link))
								Link="http://"+Link;
					StringSalida.append("<li> <span class=\"_Type "+tipo+"\">"+((CompleteElementType)completeST).getName()+": </span>"
									+"<a class=\"_LinkedRef "+tipo+"V "+tipo+"A \" href=\""+Link+"\" target=\"_blank\">"
									+Link+"</a></li>");
					
				
					}
					}
				else if (E instanceof CompleteResourceElementFile)
					{
					CompleteFile Linked=((CompleteResourceElementFile) E).getValue();
					
					
					File IconF=new File(SOURCE_FOLDER+File.separator+completeDocuments.getClavilenoid());
					IconF.mkdirs();
					
					String Path=StaticFunctionsSCORM.calculaIconoString(Linked.getPath());
					
					
					String[] spliteStri=Path.split("/");
					String NameS = spliteStri[spliteStri.length-1];
					String Icon=SOURCE_FOLDER+File.separator+completeDocuments.getClavilenoid()+File.separator+NameS;
					
					try {
						
						
					NameS=URLEncoder.encode(NameS, "UTF-8");
					
				
					
					
					File test=new File(Icon);
					while (test.exists())
						{
						NameS = "rep_"+(contadorFiles++)+spliteStri[spliteStri.length-1];
						Icon=SOURCE_FOLDER+File.separator+completeDocuments.getClavilenoid()+File.separator+NameS;
						NameS=URLEncoder.encode(NameS, "UTF-8");
						
						
						test=new File(Icon);
						}
					
					
			
						URL url2 = new URL(Path);
//						 CL.getLogLines().add("Image copy, file with url ->>"+url2.toString()+"");
//							url2=parseusrl(url2);
//							CL.getLogLines().add("Image copy, file with url ->>"+url2.toString()+"");
						saveImage(url2, Icon);
					} catch (Exception e) {
						e.printStackTrace();
						CL.getLogLines().add("Error in Image copy, file with url ->> "+Linked.getPath()+" not found or restringed");
					}
					
					int width= 50;
					int height=50;
					int widthmini= 50;
					int heightmini=50;
					
					try {
						BufferedImage bimg = ImageIO.read(new File(Icon));
						width= bimg.getWidth();
						height= bimg.getHeight();
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					
					 widthmini= 50;
					 heightmini= (50*height)/width;
					
					StringSalida.append("<li> <span class=\"_Type "+tipo+"\">"+((CompleteElementType)completeST).getName()+":</span> " +
//							"File Linked ->"+
							"<a class=\"_LinkedRef "+tipo+"V "+tipo+"A  \" href=\""+completeDocuments.getClavilenoid()+File.separator+NameS+"\" target=\"_blank\">"+
							" <img class=\"_ImagenFile "+tipo+"V \" class=\"ImagenOV\" src=\""+completeDocuments.getClavilenoid()+File.separator+NameS+"\"" +
									" onmouseover=\"this.width="+width+";this.height="+height+";\" onmouseout=\"this.width="+widthmini+";this.height="+heightmini+";\" width=\""+widthmini+"\" height=\""+heightmini+"\" alt=\""+completeDocuments.getClavilenoid()+File.separator+NameS+"\" />" +
									"</a></li>");	
				
					}
				else 
				{
					if (completeST.isSelectable())
						{
						StringSalida.append("<li> <span class=\"_Value "+tipo+"\">"+((CompleteElementType)completeST).getName()+"</span></li>");
					
						}


				}
//				else 
//					Vacio=true;
				
				}
			else

				Vacio=true;

			
			StringBuffer Hijos=new StringBuffer();
			for (CompleteElementType hijo : completeST.getSons()) {
				
				if (StaticFunctionsSCORM.isVisible(hijo))
				{
				
					
					if (StaticFunctionsSCORM.isMap(hijo)&&StaticFunctionsSCORM.hasValuedChildren(hijo,completeDocuments.getDescription()))	
					{
						
						Double Lat;
						Double Long;
						
						Lat=StaticFunctionsSCORM.getLat(hijo.getSons(),completeDocuments);
						Long=StaticFunctionsSCORM.getLong(hijo.getSons(),completeDocuments);
						
						if (Lat!=null&&Long!=null)
						
						{
						String mapOrderString="map"+nummap++;
						//CASO MAPAS
						
						StringSalida.append("	<div id=\""+mapOrderString+"\" class=\"map\">");
						StringSalida.append("<script> \n"+
					    "var map = new GMaps({ \n"+
					    "el: '#"+mapOrderString+"', \n"+
					    "lat: "+Lat+", \n"+
					    "lng: "+Long+", \n"+
					    "zoom: 10 \n"+
					    "}); \n");
						
						StringBuffer StructBufH = new StringBuffer();
						
						StructBufH.append("	<div class=\"hijomap\">");
						
						for (CompleteElementType CETY : hijo.getSons()) {
							if (!StaticFunctionsSCORM.isLatitude(CETY)&&!StaticFunctionsSCORM.isLongitude(CETY))
							{
								String Salida = processST(CETY,completeDocuments,listaLinkeados);
								if (!Salida.isEmpty())
									StructBufH.append(Salida);
								
							}
								
						}
						
						StructBufH.append("	</div>");
						
						
						StringSalida.append("map.addMarker({ \n"+
						"lat: "+Lat+", \n"+
						"lng: "+Long+", \n"+
						"infoWindow: { \n"+
						"	  content: '"+StructBufH.toString()+"'"+
						"	}\n"+
						"});");
						
						StringSalida.append("</script>");
						
						StringSalida.append("	</div>");

						}
					}
					else
					{
					
					
					
				String result2 = processST(hijo, completeDocuments,listaLinkeados);
				
				if (!result2.isEmpty())
					Hijos.append(result2.toString());
				
					}
				}
					
			}	
			
			String HijosSalida = Hijos.toString();
			
			

			
			if (!HijosSalida.isEmpty()&&Vacio)
			{
			StringSalida.append("<li> <span class=\"_Type "+tipo+"\">"+((CompleteElementType)completeST).getName()+":</span> </li>");
			
			}
		
		if (!HijosSalida.isEmpty())
			{
			StringSalida.append("<ul class=\"_List "+tipo+"\">");
			StringSalida.append(HijosSalida);
			StringSalida.append("</ul>");
			}
			


		
		
		return StringSalida.toString();
		
	}



private boolean Valorable(CompleteElementType completeST) {
		if (completeST instanceof CompleteTextElementType)
			return true;
		if (completeST instanceof CompleteLinkElementType)
			return true;
		if (completeST instanceof CompleteResourceElementType)
			return true;
		if (completeST instanceof CompleteTextElementType)
			return true;
		
		return completeST.isSelectable();
	}

//	protected HashSet<Integer> calculaAmbitos(ArrayList<Integer> ambitos,
//			CompleteElementType completeST, CompleteDocuments completeDocuments) {
//		HashSet<Long> hijos=new HashSet<Long>();
//		calculaHijos(completeST,hijos);
//		HashSet<Integer> Salida=new HashSet<Integer>();
//		int ultimo=ambitos.size();
//		for (CompleteElement element : completeDocuments.getDescription()) {
//			if (hijos.contains(element.getHastype().getClavilenoid())&&element.getAmbitos().size()>ultimo)
//				if (!Salida.contains(element.getAmbitos().get(ultimo)))
//					Salida.add(element.getAmbitos().get(ultimo));
//				
//				
//		}
//		return Salida;
//	}
//
//	private void calculaHijos(CompleteElementType completeST, HashSet<Long> hijos) {
//		if (!hijos.contains(completeST.getClavilenoid()))
//			hijos.add(completeST.getClavilenoid());
//		for (CompleteElementType hijo : completeST.getSons()) {
//			calculaHijos(hijo,hijos);
//		}
//	}
//
//	protected CompleteElement findElem(CompleteElementType completeST, List<CompleteElement> description,
//			ArrayList<Integer> ambitos) {
//		for (CompleteElement elementos : description) {
//			if (elementos.getHastype().getClavilenoid().equals(completeST.getClavilenoid())&&validos(elementos.getAmbitos(),ambitos))
//				return elementos;
//		}
//		return null;
//	}
//
//	private boolean validos(ArrayList<Integer> documento,
//			ArrayList<Integer> actual) {
//		if (actual.size()>documento.size())
//			return false;
//		
//		for (int i = 0; i < actual.size(); i++) {
//			if (!actual.get(i).equals(documento.get(i)))
//				return false;
//		}
//		
//		return true;
//	}

	
	protected CompleteElement findElem(CompleteElementType completeST, List<CompleteElement> description) {
for (CompleteElement elementos : description) {
	if (elementos.getHastype().getClavilenoid().equals(completeST.getClavilenoid()))
		return elementos;
}
return null;
}
	


	protected ArrayList<CompleteGrammar> ProcesaGramaticas(
			List<CompleteGrammar> metamodelGrammar) {
		ArrayList<CompleteGrammar> Salida = new ArrayList<CompleteGrammar>();
		for (CompleteGrammar completeGrammar : metamodelGrammar) {
			if (!IsIgnore(completeGrammar.getViews()))
				Salida.add(completeGrammar);
			
			if (IsQuiz(completeGrammar.getViews()))
				Quizz=completeGrammar;
		}
		
		return Salida;
	}
	
	private boolean IsQuiz(ArrayList<CompleteOperationalValueType> views) {
		for (CompleteOperationalValueType completeOperationalValueType : views) {
			if (completeOperationalValueType.getView().toLowerCase().equals("SCORM".toLowerCase())&&completeOperationalValueType.getName().toLowerCase().equals("Quiz".toLowerCase()))
				try {
					boolean Salida = Boolean.parseBoolean(completeOperationalValueType.getDefault().toLowerCase());
					return Salida;
				} catch (Exception e) {
					// TODO: handle exception
				}
				
		}
		return false;
	}

	private boolean IsIgnore(ArrayList<CompleteOperationalValueType> views) {
		for (CompleteOperationalValueType completeOperationalValueType : views) {
			if (completeOperationalValueType.getView().toLowerCase().equals("SCORM".toLowerCase())&&completeOperationalValueType.getName().toLowerCase().equals("ignore".toLowerCase()))
				try {
					boolean Salida = Boolean.parseBoolean(completeOperationalValueType.getDefault().toLowerCase());
					return Salida;
				} catch (Exception e) {
					// TODO: handle exception
				}
				
		}
		return false;
	}

	public static boolean testLink(String baseURLOda2) {
		if (baseURLOda2==null||baseURLOda2.isEmpty())
			return true;
		 Matcher matcher = regexAmbito.matcher(baseURLOda2);
		return matcher.matches();
	}
	
	
	protected String ReduceString(String description) {
		StringBuffer SB=new StringBuffer();
		for (int i = 0; i < description.length() && SB.length()<25; i++) {
			if ((description.charAt(i)>='A'&&description.charAt(i)<='z')||(description.charAt(i)>='A'&&description.charAt(i)<='Z'))
				SB.append(description.charAt(i));
		}
		return SB.toString();
	}
	
	
	protected String CreateNameCSS(String tipo, CompleteElementType completeST) {
		int i=0;
		String Base=tipo;
		while (NameCSS.get(Base+i)!=null&&NameCSS.get(Base+i)!=completeST)
			i++;
		return Base+i;
	}
	
	
	public static String textToHtmlConvertingURLsToLinks(String text) {
	    if (text == null) {
	        return text;
	    }



	    return text.replaceAll("(\\A|\\s)((http|https|ftp|mailto):\\S+)(\\s|\\z)",
	        "$1<a href=\"$2\">$2</a>$4");
	}
	
	
	
	
	public static void main(String[] args) {
		String message="Exception .clavy-> Params Null ";
		try {

			String fileName;
			if (args.length>0)
				fileName=args[0];
			else
				fileName = "test.clavy";
			 
			System.out.println(fileName);
			 

			 File file = new File(fileName);
			 FileInputStream fis = new FileInputStream(file);
			 ObjectInputStream ois = new ObjectInputStream(fis);
			 CompleteCollection object = (CompleteCollection) ois.readObject();
			 
			 
			 try {
				 ois.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			 try {
				 fis.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
			 
			 String Folder = System.getProperty("user.home")+File.separator+System.nanoTime();
		
			 new File(Folder).mkdirs();
			 
			 
		 
			 List<Long> List = new ArrayList<>();
			 
			 //36297
			 if (args.length>1)
					 for (int i = 1; i < args.length; i++) {
						try {
							List.add(Long.parseLong(args[i]));
						} catch (Exception e) {
							// TODO: handle exception
						}
					}


			 
			SCORMPprocess SP=new SCORMPprocess(List ,object,Folder,new CompleteLogAndUpdates(),"Test");
			SP.preocess();
			 
	    }catch (Exception e) {
			e.printStackTrace();
			System.err.println(message);
			throw new RuntimeException(message);
		}
		  
		  
	}
	
	

}
