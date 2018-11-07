package fdi.ucm.server.exportparser.IMSCP;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import fdi.ucm.server.modelComplete.collection.document.CompleteDocuments;
import fdi.ucm.server.modelComplete.collection.document.CompleteElement;
import fdi.ucm.server.modelComplete.collection.document.CompleteResourceElementFile;
import fdi.ucm.server.modelComplete.collection.document.CompleteResourceElementURL;
import fdi.ucm.server.modelComplete.collection.document.CompleteTextElement;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteElementType;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteGrammar;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteOperationalValueType;



public class StaticFunctionsSCORM {

	private static final String CLAVYICONOS = "http://a-note.fdi.ucm.es/Clavy/";

	public static String calculaIconoString(String textopath) {
		if
		(
				//Imagen
		textopath.toLowerCase().endsWith(".jpg")
		||
		textopath.toLowerCase().endsWith(".jpge")	
		||
		textopath.toLowerCase().endsWith(".gif")
		||
		textopath.toLowerCase().endsWith(".png")
		)
		return textopath;
	else
		if (textopath.toLowerCase().endsWith(".rar"))
			return CLAVYICONOS+StaticIconos.ICONORAR;
	else
		if (textopath.toLowerCase().endsWith(".avi"))
			return CLAVYICONOS+StaticIconos.ICONOAVI;
		else
			if (textopath.toLowerCase().endsWith(".doc"))
				return CLAVYICONOS+StaticIconos.ICONODOC;
			else
				if (textopath.toLowerCase().endsWith(".docx"))
					return CLAVYICONOS+StaticIconos.ICONODOCX;
				else
					if (textopath.toLowerCase().endsWith(".pdf"))
						return CLAVYICONOS+StaticIconos.ICONOPDF;
					else
						if (textopath.toLowerCase().endsWith(".html"))
							return CLAVYICONOS+StaticIconos.ICONOHTML;
						else
							if (textopath.toLowerCase().endsWith(".htm"))
								return CLAVYICONOS+StaticIconos.ICONOHTML;
							else
								if (textopath.toLowerCase().endsWith(".php"))
									return CLAVYICONOS+StaticIconos.ICONOHTML;
								else
									if (textopath.toLowerCase().endsWith(".ppt"))
										return CLAVYICONOS+StaticIconos.ICONOPPT;
									else
										if (textopath.toLowerCase().endsWith(".pptx"))
											return CLAVYICONOS+StaticIconos.ICONOPPTX;
										else
											if (textopath.toLowerCase().endsWith(".mov"))
												return CLAVYICONOS+StaticIconos.ICONOMOV;
											else
												if (textopath.toLowerCase().endsWith(".fla"))
													return CLAVYICONOS+StaticIconos.ICONOFLA;
												else
													if (textopath.toLowerCase().endsWith(".swf"))
														return CLAVYICONOS+StaticIconos.ICONOSWF;
													else
														if (textopath.toLowerCase().endsWith(".midi"))
															return CLAVYICONOS+StaticIconos.ICONOMIDI;
														else
															if (textopath.toLowerCase().endsWith(".mp3"))
																return CLAVYICONOS+StaticIconos.ICONOMP3;
															else
																if (textopath.toLowerCase().endsWith(".mp4"))
																	return CLAVYICONOS+StaticIconos.ICONOMP4;
																else
																	if (textopath.toLowerCase().endsWith(".mpg"))
																		return CLAVYICONOS+StaticIconos.ICONOMPG;
																	else
																		if (textopath.toLowerCase().endsWith(".odt"))
																			return CLAVYICONOS+StaticIconos.ICONOODT;
																		else
																			if (textopath.toLowerCase().endsWith(".ods"))
																				return CLAVYICONOS+StaticIconos.ICONOODS;
																			else
																				if (textopath.toLowerCase().endsWith(".zip"))
																					return CLAVYICONOS+StaticIconos.ICONOZIP;
																				else
																					if (textopath.toLowerCase().endsWith(".rtf"))
																						return CLAVYICONOS+StaticIconos.ICONORTF;
																					else
																						if (textopath.toLowerCase().endsWith(".ttf"))
																							return CLAVYICONOS+StaticIconos.ICONOTTF;
																						else
																							if (textopath.toLowerCase().endsWith(".txt"))
																								return CLAVYICONOS+StaticIconos.ICONOTXT;
																							else
																								if (textopath.toLowerCase().endsWith(".wav"))
																									return CLAVYICONOS+StaticIconos.ICONOWAV;
																								else
																									if (textopath.toLowerCase().endsWith(".wma"))
																										return CLAVYICONOS+StaticIconos.ICONOWMA;
																									else
																										if (textopath.toLowerCase().endsWith(".wmv"))
																											return CLAVYICONOS+StaticIconos.ICONOWMV;
																										else
																											if (textopath.toLowerCase().endsWith(".xls"))
																												return CLAVYICONOS+StaticIconos.ICONOXLS;
																											else
																												if (textopath.toLowerCase().endsWith(".xlsx"))
																													return CLAVYICONOS+StaticIconos.ICONOXLSX;
																												else
																													if (textopath.toLowerCase().endsWith(".xml"))
																														return CLAVYICONOS+StaticIconos.ICONOXML;
	
		return CLAVYICONOS+StaticIconos.ICONODEFAULT;
											
	}

	public static String calculaIconoStringURL() {
		return CLAVYICONOS+StaticIconos.ICONOHTML;
	}
	
	public static boolean isInGrammar(CompleteDocuments iterable_element,
			CompleteGrammar completeGrammar) {
		HashSet<Long> ElemT=new HashSet<Long>();
		for (CompleteElement dd : iterable_element.getDescription()) {
			ElemT.add(dd.getHastype().getClavilenoid());
		}
		
		return isInGrammar(ElemT, completeGrammar.getSons());
		
		
	}
	
	
	private static boolean isInGrammar(HashSet<Long> elemT,
			List<CompleteElementType> sons) {
		for (CompleteElementType CSlong1 : sons) {
			if (elemT.contains(CSlong1.getClavilenoid())||isInGrammar(elemT, CSlong1.getSons()))
				return true;
			
		}
		return false;
	}

	public static boolean isimage(String textopath) {
		if
		(
				//Imagen
		textopath.toLowerCase().endsWith(".jpg")
		||
		textopath.toLowerCase().endsWith(".jpge")	
		||
		textopath.toLowerCase().endsWith(".gif")
		||
		textopath.toLowerCase().endsWith(".png")
		)
			return true;
		else
				
		return false;
	}

	public static boolean isVisible(CompleteElementType completeST) {
		for (CompleteOperationalValueType show : completeST.getShows()) {
			if (show.getView().toLowerCase().equals("scorm")&&show.getName().toLowerCase().equals("visible"))
				return !show.getDefault().toLowerCase().equals("false");
				
		}
		return true;
	}

	public static boolean isMap(CompleteElementType completeST) {
		for (CompleteOperationalValueType show : completeST.getShows()) {
			if (show.getView().toLowerCase().equals("clavy")&&show.getName().toLowerCase().equals("editor"))
				return show.getDefault().toLowerCase().equals("gmaps");
				
		}
		return false;
	}

	public static boolean hasValuedChildren(CompleteElementType completeST,
			List<CompleteElement> description) {
		boolean latitudV=false;
		boolean longitudV=false;
		for (CompleteElementType elemtyh : completeST.getSons()) {
			if (isLatitude(elemtyh))
				{
				for (CompleteElement completeElement : description) {
					if (completeElement.getHastype().getClavilenoid().equals(elemtyh.getClavilenoid()))
						{
						latitudV=true;
						break;
						}
				}
				}
			
			if (isLongitude(elemtyh))
			{
			for (CompleteElement completeElement : description) {
				if (completeElement.getHastype().getClavilenoid().equals(elemtyh.getClavilenoid()))
					{
					longitudV=true;
					break;
					}
			}
			}
		}
		
		return latitudV&&longitudV;
	}

	public static boolean isLatitude(CompleteElementType completeST) {
		for (CompleteOperationalValueType show : completeST.getShows()) {
			if (show.getView().toLowerCase().equals("clavy")&&show.getName().toLowerCase().equals("gmaps"))
				return show.getDefault().toLowerCase().equals("latitude");
				
		}
		return false;
	}

	public static boolean isLongitude(CompleteElementType completeST) {
		for (CompleteOperationalValueType show : completeST.getShows()) {
			if (show.getView().toLowerCase().equals("clavy")&&show.getName().toLowerCase().equals("gmaps"))
				return show.getDefault().toLowerCase().equals("longitude");
				
		}
		return false;
	}

	public static java.lang.Double getLat(List<CompleteElementType> sons,
			CompleteDocuments completeDocuments) {
		try {
			for (CompleteElementType elemtyh : sons) {
				if (isLatitude(elemtyh))
					for (CompleteElement completeElement : completeDocuments.getDescription()) {
						if (completeElement.getHastype().getClavilenoid().equals(elemtyh.getClavilenoid()))
							return Double.parseDouble(((CompleteTextElement)completeElement).getValue());
					}
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}
	
	public static java.lang.Double getLong(List<CompleteElementType> sons,
			CompleteDocuments completeDocuments) {
		try {
			for (CompleteElementType elemtyh : sons) {
				if (isLongitude(elemtyh))
					for (CompleteElement completeElement : completeDocuments.getDescription()) {
						if (completeElement.getHastype().getClavilenoid().equals(elemtyh.getClavilenoid()))
							return Double.parseDouble(((CompleteTextElement)completeElement).getValue());
					}
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	public static boolean isQOptions(CompleteElementType completeST) {
		for (CompleteOperationalValueType completeOperationalValueType : completeST.getShows()) {
			if (completeOperationalValueType.getView().toLowerCase().equals("SCORM".toLowerCase())&&completeOperationalValueType.getName().toLowerCase().equals("Options".toLowerCase()))
				try {
					boolean Salida = Boolean.parseBoolean(completeOperationalValueType.getDefault().toLowerCase());
					return Salida;
				} catch (Exception e) {
					// TODO: handle exception
				}
				
		}
		return false;
	}

	public static boolean isQAnswer(CompleteElementType completeST) {
		for (CompleteOperationalValueType completeOperationalValueType : completeST.getShows()) {
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

	public static String getDefault() {
		return CLAVYICONOS+StaticIconos.ICONODEFAULT;
	}

	public static String calculaIconoStringDefault(CompleteGrammar completeST) {
		for (CompleteOperationalValueType completeOperationalValueType : completeST.getViews()) 
			if (completeOperationalValueType.getView().toLowerCase().equals("ICON".toLowerCase())&&completeOperationalValueType.getName().toLowerCase().equals("Default".toLowerCase())&&completeOperationalValueType.getDefault().trim().isEmpty())
					return completeOperationalValueType.getDefault();

				
		
		return null;
	}
	
	
	public static List<Long> getOptions(ArrayList<CompleteElementType> elemtq) {
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

	

	public static int getSolution(List<CompleteElementType> elemtq, List<CompleteElement> description) {
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

	public static String getQuestion(List<CompleteElementType> elemtq, List<CompleteElement> description) {
		
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

	public static boolean IsQuestion(ArrayList<CompleteOperationalValueType> shows) {
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
	
	
	public static boolean IsOptions(ArrayList<CompleteOperationalValueType> shows) {
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
	
	public static boolean IsAnswer(ArrayList<CompleteOperationalValueType> shows) {
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
	
	public static String getImage(List<CompleteElementType> elemtq, List<CompleteElement> description) {
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

	public static boolean IsImage(ArrayList<CompleteOperationalValueType> shows) {
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

	
	
	public static boolean IsQuiz(ArrayList<CompleteOperationalValueType> views) {
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

	public static boolean IsIgnore(ArrayList<CompleteOperationalValueType> views) {
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
	
}
