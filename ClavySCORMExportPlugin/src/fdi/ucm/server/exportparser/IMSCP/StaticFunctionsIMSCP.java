package fdi.ucm.server.exportparser.IMSCP;

import java.util.HashSet;
import java.util.List;

import fdi.ucm.server.modelComplete.collection.document.CompleteDocuments;
import fdi.ucm.server.modelComplete.collection.document.CompleteElement;
import fdi.ucm.server.modelComplete.collection.document.CompleteTextElement;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteElementType;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteGrammar;
import fdi.ucm.server.modelComplete.collection.grammar.CompleteOperationalValueType;



public class StaticFunctionsIMSCP {

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
			if (show.getView().toLowerCase().equals("imscp")&&show.getName().toLowerCase().equals("visible"))
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

}
