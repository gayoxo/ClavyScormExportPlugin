/**
 * 
 */
package fdi.ucm.server.exportparser.IMSCP;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
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
import fdi.ucm.server.modelComplete.collection.grammar.CompleteResourceElementType;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteTextElementType;


/**
 * @author Joaquin Gayoso-Cabada
 *
 */
public class IMSCPprocess {


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
	private int contadorRec;
	private String TextoEntrada;
	private int contadorFiles;
	protected static final String CLAVY="OdAClavy";
	protected HashMap<Long,String> TablaHTML;
	protected HashMap<Long,HashSet<CompleteDocuments>> TablaHTMLLink;
	private HashSet<CompleteDocuments> ProcesadosGeneral;
	private int contador_IDs;
	private HashMap<CompleteGrammar, HashSet<CompleteDocuments>> Gram_doc;
	private int nummap;

	public IMSCPprocess(List<Long> listaDeDocumentos, CompleteCollection salvar, String sOURCE_FOLDER, CompleteLogAndUpdates cL, String entradaText) {
		
		
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
			if (DocumentoT.contains(docuemntos.getClavilenoid()))
			completeDocuments.addFirst(docuemntos);
	
		
		

		if (completeDocuments.isEmpty())
			{
			CL.getLogLines().add("Error, documento no existe");
			return;
			}
		
		Recursos=new HashMap<String,String>();
		
		try {
			
			contadorRec=0;
			contadorFiles=0;
			contador_IDs=0;
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        DOMImplementation implementation = builder.getDOMImplementation();
	        Document document = implementation.createDocument(null, "manifest", null);
	        document.setXmlVersion("1.0");
	        
	        ArrayList<CompleteGrammar> GramaticasAProcesar=ProcesaGramaticas(Salvar.getMetamodelGrammar());
	        
	        Element manifest = document.getDocumentElement();
	        
	        {
		        Attr Atr = document.createAttribute("xmlns");
		        Atr.setValue("http://www.imsglobal.org/xsd/imscp_v1p1");
		        manifest.setAttributeNode(Atr);
		        }
		        
		        {
			    Attr Atr = document.createAttribute("xmlns:imsmd");
			    Atr.setValue("http://www.imsglobal.org/xsd/imsmd_v1p2");
			    manifest.setAttributeNode(Atr);
			    }

		        {
			    Attr Atr = document.createAttribute("xmlns:xsi");
			    Atr.setValue("http://www.w3.org/2001/XMLSchema-instance");
			    manifest.setAttributeNode(Atr);
			    }
		        
		        {
			    Attr Atr = document.createAttribute("xsi:schemaLocation");
			    Atr.setValue("http://www.imsglobal.org/xsd/imscp_v1p1 ../xsds/imscp_v1p2.xsd http://www.imsglobal.org/xsd/imsmd_v1p2 http://www.imsglobal.org/xsd/imsmd_v1p2p4.xsd ");
			    manifest.setAttributeNode(Atr);
			    }
		        
		        {
			    Attr Atr = document.createAttribute("identifier");
			    Atr.setValue("CLAVY_MAINFEST"+SN);
			    manifest.setAttributeNode(Atr);
			    }
		        
		        
		        {
				Attr Atr = document.createAttribute("version");
				Atr.setValue("IMS CP 1.2");
				manifest.setAttributeNode(Atr);
				}
	        
	        metadata = document.createElement("metadata"); 
	        organizations = document.createElement("organizations"); 
	        resources = document.createElement("resources"); 
	        
	        manifest.appendChild(metadata);
	        manifest.appendChild(organizations);
	        manifest.appendChild(resources);
	        
	        processMetadata(document);
	        String Main_S=processOrganization(completeDocuments,document,GramaticasAProcesar);
	        {
				Attr Atr = document.createAttribute("default");
				Atr.setValue(Main_S);
				organizations.setAttributeNode(Atr);
			}
	        processResources(document);
	        
	        
	        
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
		        
		        
		        {
			        Attr Atr = document.createAttribute("href");
			        Atr.setValue(recursotable.getValue());
			        ResourceUni.setAttributeNode(Atr);
			        }
			        
		        Element FileUni = document.createElement("file"); 
		        ResourceUni.appendChild(FileUni);
		        
		        {
			        Attr Atr = document.createAttribute("href");
			        Atr.setValue(recursotable.getValue());
			        FileUni.setAttributeNode(Atr);
			        }
		        
			
		}
		
	}

	private String processOrganization(
			LinkedList<CompleteDocuments> completeDocumentsList, 
			Document document,ArrayList<CompleteGrammar> GramaticasAProcesar) {
			
			String NameBlock="MAIN_TOC"+completeDocumentsList.get(0).getClavilenoid();
			
			Element Organization = document.createElement("organization"); 
			organizations.appendChild(Organization);
			
				{
		        Attr Atr = document.createAttribute("identifier");
		        Atr.setValue(NameBlock);
		        Organization.setAttributeNode(Atr);
		        }
		        
		        {
			    Attr Atr = document.createAttribute("structure");
			    Atr.setValue("hierarchical");
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

			     Text nodeKeyValueTiL = document.createTextNode("Total List");
			     TitleIL.appendChild(nodeKeyValueTiL);
			     
			     {
				        Attr Atr = document.createAttribute("identifier");
				        Atr.setValue("Total List");
				        ItemList.setAttributeNode(Atr);
				        }
				        
				        {
				      
				        String MAINSTR="MAIN_RESOURCE"+(contadorRec);	
					    Attr Atr = document.createAttribute("identifierref");
					    Atr.setValue(MAINSTR);
					    ItemList.setAttributeNode(Atr);
					    }
		     
			     
			   Gram_doc=new HashMap<CompleteGrammar, HashSet<CompleteDocuments>>();
		     
		     while (!completeDocumentsList.isEmpty())
		     {
		    	 CompleteDocuments completeDocuments =completeDocumentsList.removeLast();
		    	 
		    	 if (!ProcesadosGeneral.contains(completeDocuments))
		    	{
		    	 ProcesadosGeneral.add(completeDocuments);
//		     }
//		     for (CompleteDocuments completeDocuments : completeDocumentsList) {
		     
		     ArrayList<CompleteGrammar> GramaticasAplicadas=new ArrayList<CompleteGrammar>();
		     
		     for (CompleteGrammar completeGrammar : GramaticasAProcesar) {
					if (StaticFunctionsIMSCP.isInGrammar(completeDocuments,completeGrammar))
						GramaticasAplicadas.add(completeGrammar);
				
				
				}
		    
		     
		     for (CompleteGrammar completeGrammar : GramaticasAplicadas) {
		    	 
		    	 
		    	 HashSet<CompleteDocuments> DocGram=Gram_doc.get(completeGrammar);
		    	 
		    	 if (DocGram==null)
		    		 DocGram=new HashSet<CompleteDocuments>();
		    	 
		    	 DocGram.add(completeDocuments);
		    	 
		    	 Gram_doc.put(completeGrammar, DocGram);
		    	 
		     } 
		     
		     
		     String Grammarname="ungrammar";
	    	 if (GramaticasAplicadas.size()>0)
	    		 Grammarname=GramaticasAplicadas.get(0).getNombre();
		     
		    	 
		    	 HashSet<CompleteDocuments> ListaLinkeados=new HashSet<CompleteDocuments>();
		    	 HashSet<CompleteDocuments> Procesados = new HashSet<CompleteDocuments>();
		    	 Procesados.add(completeDocuments);
		    	 
		    	 
			     
		    	 
		    	 Element Item = document.createElement("item"); 
		    	 ItemList.appendChild(Item);
			     
			     {
				        Attr Atr = document.createAttribute("identifier");
				        Atr.setValue(Grammarname+": "+completeDocuments.getClavilenoid()+"_"+contador_IDs++);
				        Item.setAttributeNode(Atr);
				        }
				        
				        {
				      
				        String MAINSTR="MAIN_RESOURCE"+(contadorRec++);	
				        String Recurso=ProcessFileHTML(completeDocuments,GramaticasAplicadas,ListaLinkeados);
					    Attr Atr = document.createAttribute("identifierref");
					    Atr.setValue(MAINSTR);
					    Item.setAttributeNode(Atr);
					    Recursos.put(MAINSTR, Recurso);
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
										if (StaticFunctionsIMSCP.isInGrammar(completedocHijo,completeGrammar2))
											completeGrammarLHijo.add(completeGrammar2);
									
									
									}
									
									
									processItem(Item,completedocHijo,completeGrammarLHijo,document,Procesados);
									
								}
							
					     
					     
			}
		     
		     }
		     
		     }
		     
		     //SOLO SI HAY MAS DE UNA GRAMTICA
		     if (Gram_doc.keySet().size()>1)
		     for (Entry<CompleteGrammar, HashSet<CompleteDocuments>> grupillo : Gram_doc.entrySet()) {
		    	 Element ItemListG = document.createElement("item"); 
			     Organization.appendChild(ItemListG);
			     
			     Element TitleILG = document.createElement("title"); 
			     ItemListG.appendChild(TitleILG);

				     Text nodeKeyValueTiLG = document.createTextNode("List of: "+grupillo.getKey().getNombre());
				     TitleILG.appendChild(nodeKeyValueTiLG);
				     
				     
				     {
					        Attr Atr = document.createAttribute("identifier");
					        Atr.setValue("List of: "+grupillo.getKey().getNombre()+(contador_IDs++));
					        ItemListG.setAttributeNode(Atr);
					        }
					        
					        {
					      
					        String MAINSTR="MAIN_RESOURCE"+(contadorRec);	
						    Attr Atr = document.createAttribute("identifierref");
						    Atr.setValue(MAINSTR);
						    ItemListG.setAttributeNode(Atr);
						    }

				    	
//				     }
				     for (CompleteDocuments completeDocuments : grupillo.getValue()) {
				     
				    	 
				    	 
//				    	 HashSet<CompleteDocuments> DocGram=Gram_doc.get(completeGrammar);
//				    	 
//				    	 if (DocGram==null)
//				    		 DocGram=new HashSet<CompleteDocuments>();
//				    	 
//				    	 DocGram.add(completeDocuments);
//				    	 
//				    	 Gram_doc.put(completeGrammar, DocGram);
				    	 
				    	 
				    	 
				    	 HashSet<CompleteDocuments> ListaLinkeados=new HashSet<CompleteDocuments>();
				    	 HashSet<CompleteDocuments> Procesados = new HashSet<CompleteDocuments>();
				    	 Procesados.add(completeDocuments);
				    	 
				    	 
					     
				    	 
				    	 Element Item = document.createElement("item"); 
				    	 ItemListG.appendChild(Item);
					     
					     {
						        Attr Atr = document.createAttribute("identifier");
						        Atr.setValue(grupillo.getKey().getNombre()+"_"+grupillo.getKey().getNombre()+": "+completeDocuments.getClavilenoid()+"_"+contador_IDs++);
						        Item.setAttributeNode(Atr);
						        }
						        
						        {
						      
						        String MAINSTR="MAIN_RESOURCE"+(contadorRec++);	
						        List<CompleteGrammar> lista=new ArrayList<>();
						        lista.add(grupillo.getKey());
						        String Recurso=ProcessFileHTML(completeDocuments,lista,ListaLinkeados);
							    Attr Atr = document.createAttribute("identifierref");
							    Atr.setValue(MAINSTR);
							    Item.setAttributeNode(Atr);
							    Recursos.put(MAINSTR, Recurso);
							    }
						        
						        Element TitleI = document.createElement("title"); 
						        Item.appendChild(TitleI);
						        
						        String CuterTiyle=grupillo.getKey().getNombre()+": "+completeDocuments.getDescriptionText();
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
												if (StaticFunctionsIMSCP.isInGrammar(completedocHijo,completeGrammar2))
													completeGrammarLHijo.add(completeGrammar2);
											
											
											}
											
											
											processItem(Item,completedocHijo,completeGrammarLHijo,document,Procesados);
											
										}

							     
							     
					}
				     
				     }
				     
			}
		     
		     
		
		return NameBlock;
	}





	private void processItem(Element item, CompleteDocuments completeDocuments,
			ArrayList<CompleteGrammar> GramaticasAplicadas, Document document, HashSet<CompleteDocuments> procesados) {
		
	    	 
	    	 HashSet<CompleteDocuments> ListaLinkeados=new HashSet<CompleteDocuments>();
	    	 HashSet<CompleteDocuments> Procesados=new HashSet<CompleteDocuments>(procesados);
	    	 
	    	 
	    	 
	    	 
	    	 for (CompleteGrammar completeGrammar : GramaticasAplicadas) {
	    	 
	    	 HashSet<CompleteDocuments> DocGram=Gram_doc.get(completeGrammar);
	    	 
	    	 if (DocGram==null)
	    		 DocGram=new HashSet<CompleteDocuments>();
	    	 
	    	 DocGram.add(completeDocuments);
	    	 
	    	 Gram_doc.put(completeGrammar, DocGram);
	    	 
	    	 }
	    	 
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
									if (StaticFunctionsIMSCP.isInGrammar(completedocHijo,completeGrammar2))
										completeGrammarLHijo.add(completeGrammar2);
								
								
								}
								
								
								 
						    	
								
								
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
			CodigoHTML.append("<link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"style.css\">");
			CodigoHTML.append("<meta name=\"description\" content=\"Informe generado por el sistema "+CLAVY+"\">");
			Calendar C=new GregorianCalendar();
			DateFormat df = new SimpleDateFormat ("yyyy-MM-dd");
			String ValueHoy = df.format(C.getTime());	
			CodigoHTML.append("<meta name=\"fecha\" content=\""+ValueHoy+"\">");
			CodigoHTML.append("<meta name=\"author\" content=\"Grupo de investigación ILSA-UCM\">");
//			CodigoHTML.append("<style>");
//			CodigoHTML.append("li.doc {color: blue;}");	
//			CodigoHTML.append("</style>");
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
			
			
			String Path=StaticFunctionsIMSCP.calculaIconoString(completeDocuments.getIcon());
			
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

			Document.append("<li> <span class=\"_Type Icon N_1\">Icon:</span> <img class=\"Icon _Value N_1V\" src=\""+
			completeDocuments.getClavilenoid()+File.separator+NameS+"\" onmouseover=\"this.width="+width+";this.height="+height+";\" onmouseout=\"this.width="+widthmini+";this.height="+heightmini+";\" width=\""+widthmini+"\" height=\""+heightmini+"\" alt=\""+Path+"\" /></li>");
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
					
					if (StaticFunctionsIMSCP.isVisible(completeST))
					{
					
					if (StaticFunctionsIMSCP.isMap(completeST)&&StaticFunctionsIMSCP.hasValuedChildren(completeST,completeDocuments.getDescription()))	
					{
						
						Double Lat;
						Double Long;
						
						Lat=StaticFunctionsIMSCP.getLat(completeST.getSons(),completeDocuments);
						Long=StaticFunctionsIMSCP.getLong(completeST.getSons(),completeDocuments);
						
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
							if (!StaticFunctionsIMSCP.isLatitude(CETY)&&!StaticFunctionsIMSCP.isLongitude(CETY))
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
            Text nodeKeyValue = document.createTextNode("IMS Content");	
            keyNode.appendChild(nodeKeyValue);
            metadata.appendChild(keyNode);
		}
		
		{
			Element keyNode = document.createElement("schemaversion"); 
            Text nodeKeyValue = document.createTextNode("1.2");	
            keyNode.appendChild(nodeKeyValue);
            metadata.appendChild(keyNode);
		}
		
		
		//IMS LOM
		{
			Element LOM = document.createElement("imsmd:lom"); 
            metadata.appendChild(LOM);
            
            Element General = document.createElement("imsmd:general"); 
            LOM.appendChild(General);
            
            Element Title = document.createElement("imsmd:title"); 
            General.appendChild(Title);
            
            Element Lan = document.createElement("imsmd:langstring"); 
            Title.appendChild(Lan);
            
            Attr Atr = document.createAttribute("xml:lang");
			Atr.setValue("en-US");
			Lan.setAttributeNode(Atr);
			
			Text nodeKeyValue = document.createTextNode(TextoEntrada);
			Lan.appendChild(nodeKeyValue);
		}
		
		
		
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
			 filewriter = new FileWriter(SOURCE_FOLDER+"\\style.css");//declarar el archivo
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
		
		if (!StaticFunctionsIMSCP.isVisible(completeST))
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
						
					String Path=StaticFunctionsIMSCP.calculaIconoString(Linked.getIcon());
					
					
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
					
					
					StringSalida.append("<li> <span class=\"_Type "+tipo+"\">"+((CompleteElementType)completeST).getName()+":</span> <img class=\"_ImagenOV "+tipo+"V \" src=\""+
							completeDocuments.getClavilenoid()+File.separator+NameS+
							"\" onmouseover=\"this.width="+width+";this.height="+height+";\" onmouseout=\"this.width="+widthmini+";this.height="+heightmini+
							";\" width=\""+widthmini+"\" height=\""+heightmini+"\" alt=\""+completeDocuments.getClavilenoid()+File.separator+NameS+"\" /> "+
//							"<span class=\""+tipo+"V _ClavyID _Value\">" +Linked.getClavilenoid()+"</span>"+
							"<span class=\""+tipo+"V _DescriptionRel _Value\">" +textToHtmlConvertingURLsToLinks(Linked.getDescriptionText())+"</span></li>");
					
					
					}
					}
				else if (E instanceof CompleteResourceElementURL)
					{
					String Link = ((CompleteResourceElementURL)E).getValue();
							
					
					if (StaticFunctionsIMSCP.isimage(Link.toLowerCase()))
					{
						String Path=StaticFunctionsIMSCP.calculaIconoString(Link);
						
						
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
									"<a class=\"_LinkedRef "+tipo+"V "+tipo+"A  \" href=\""+completeDocuments.getClavilenoid()+File.separator+NameS+"\" target=\"_blank\">"+
									" <img class=\"_ImagenFile "+tipo+"V \" class=\"ImagenOV\" src=\""+completeDocuments.getClavilenoid()+File.separator+NameS+"\"" +
											" onmouseover=\"this.width="+width+";this.height="+height+";\" onmouseout=\"this.width="+widthmini+";this.height="+heightmini+";\" width=\""+widthmini+"\" height=\""+heightmini+"\" alt=\""+completeDocuments.getClavilenoid()+File.separator+NameS+"\" />" +
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
					
					String Path=StaticFunctionsIMSCP.calculaIconoString(Linked.getPath());
					
					
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
				
				if (StaticFunctionsIMSCP.isVisible(hijo))
				{
				
					
					if (StaticFunctionsIMSCP.isMap(hijo)&&StaticFunctionsIMSCP.hasValuedChildren(hijo,completeDocuments.getDescription()))	
					{
						
						Double Lat;
						Double Long;
						
						Lat=StaticFunctionsIMSCP.getLat(hijo.getSons(),completeDocuments);
						Long=StaticFunctionsIMSCP.getLong(hijo.getSons(),completeDocuments);
						
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
							if (!StaticFunctionsIMSCP.isLatitude(CETY)&&!StaticFunctionsIMSCP.isLongitude(CETY))
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
		ArrayList<CompleteGrammar> Salida=new ArrayList<CompleteGrammar>();
		for (CompleteGrammar completeGrammar : metamodelGrammar) {
			Salida.add(completeGrammar);
		}
		return Salida;
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

}
