package it.polito.tdp.meteo.model;

import java.util.LinkedList;
import java.util.List;

import it.polito.tdp.meteo.DAO.*;

public class Model {
	
	private MeteoDAO meteoDAO;
	private List<String> soluzione;
	
	private int min;
	
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	
	int totale_esterno=99;

	public Model() {
		meteoDAO = new MeteoDAO();
		soluzione = new LinkedList<String>();
	}

	// of course you can change the String output with what you think works best
	public String getUmiditaMedia(int mese) {
		
		String media = "";
		
		float avg=0;
		int tot=0;
		
		List<Rilevamento> rilevamentiMilano = meteoDAO.getAllRilevamentiLocalitaMese(mese, "Milano");
		for(int i=0;i<rilevamentiMilano.size();i++) {
			tot+=rilevamentiMilano.get(i).getUmidita();
		}
		avg=(float)tot/(float)rilevamentiMilano.size();
		
		media=media+"Milano: "+avg+"\n";
		
		avg=0;
		tot=0;
		
		List<Rilevamento> rilevamentiTorino = meteoDAO.getAllRilevamentiLocalitaMese(mese, "Torino");
		for(int i=0;i<rilevamentiTorino.size();i++) {
			tot+=rilevamentiTorino.get(i).getUmidita();
		}
		avg=(float)tot/(float)rilevamentiTorino.size();
		
		media=media+"Torino: "+avg+"\n";
		
		
		avg=0;
		tot=0;
		List<Rilevamento> rilevamentiGenova = meteoDAO.getAllRilevamentiLocalitaMese(mese, "Genova");
		for(int i=0;i<rilevamentiGenova.size();i++) {
			tot+=rilevamentiGenova.get(i).getUmidita();
		}
		avg=(float)tot/(float)rilevamentiGenova.size();
		
		media=media+"Genova: "+avg+"\n";
		
		return media;
	}
	
	// of course you can change the String output with what you think works best
	public String trovaSequenza(int mese) {
		
		List<String> parziale = new LinkedList<String>();
		List<Rilevamento> rilevamentiMilano = meteoDAO.getAllRilevamentiLocalitaMese(mese, "Milano");
		List<Rilevamento> rilevamentiTorino = meteoDAO.getAllRilevamentiLocalitaMese(mese, "Torino");
		List<Rilevamento> rilevamentiGenova = meteoDAO.getAllRilevamentiLocalitaMese(mese, "Genova");
		min = 999999;
		int tot=0;
		
		trovaSequenzaRicorsivo(parziale, 0, mese, rilevamentiMilano, rilevamentiTorino, rilevamentiGenova, tot);
		
		String ris = ""+totale_esterno+"  "+soluzione.size()+"\n"; //fino a qua arriva ma soluzione è vuoot
		
		for(String si : soluzione) {
			ris=ris+si+"\n";
		}
		
	
		
		return ris;
	}
	
	public void trovaSequenzaRicorsivo(List<String> parziale, int livello, int mese, List<Rilevamento> rilevamentiMilano, List<Rilevamento> rilevamentiTorino, List<Rilevamento> rilevamentiGenova, int tot){
		
		//controllogiorniMinCitta(parziale)
		/*se metti controllogiorni non funziona provare senza si perchè 
		 * ovviamente non conviene cambiare tante volte città controllare quel metodo
		 */
		
		if(livello==15) {
			
			if(tot<min && controllogiorniMinCitta(parziale)) {
				min=tot;
				/*for(int i=0;i<parziale.size();i++) {
					soluzione.add(parziale.get(i));
				}*/
				soluzione = new LinkedList<String>(parziale);
				totale_esterno=tot;
			}
			
		} else {
			
			for(int i=0;i<3;i++) {
				
				if(i==0) {
					parziale.add("Milano");
					tot=tot+rilevamentiMilano.get(livello).getUmidita();
				}
				if(i==1) {
					parziale.add("Torino");
					tot=tot+rilevamentiTorino.get(livello).getUmidita();
				}
				if(i==2) {
					parziale.add("Genova");
					tot=tot+rilevamentiGenova.get(livello).getUmidita();
				}
				
				if(livello>=1) {
					if(parziale.get(livello-1).compareTo(parziale.get(livello))!=0) {
						tot=tot+COST;
					}
				}
								
				if(controlloGiorniMaxCitta(parziale)) {
					
					trovaSequenzaRicorsivo(parziale, livello+1, mese, rilevamentiMilano, rilevamentiTorino, rilevamentiGenova, tot);
					
					
					//ok perchè se tipo parziale 
					if(parziale.get(parziale.size()-1).compareTo("Milano")==0) {
						tot=tot-rilevamentiMilano.get(parziale.size()-1).getUmidita();
					}
					if(parziale.get(parziale.size()-1).compareTo("Torino")==0) {
						tot=tot-rilevamentiTorino.get(parziale.size()-1).getUmidita();
					}
					if(parziale.get(parziale.size()-1).compareTo("Genova")==0) {
						tot=tot-rilevamentiGenova.get(parziale.size()-1).getUmidita();
					}
					
					if(livello>=1) {
						if(parziale.get(livello-1).compareTo(parziale.get(livello))!=0) {
							tot=tot-COST;
						}
					}
					
					parziale.remove(parziale.size()-1);
					
				} else {
					
					if(parziale.get(parziale.size()-1).compareTo("Milano")==0) {
						tot=tot-rilevamentiMilano.get(parziale.size()-1).getUmidita();
					}
					if(parziale.get(parziale.size()-1).compareTo("Torino")==0) {
						tot=tot-rilevamentiTorino.get(parziale.size()-1).getUmidita();
					}
					if(parziale.get(parziale.size()-1).compareTo("Genova")==0) {
						tot=tot-rilevamentiGenova.get(parziale.size()-1).getUmidita();
					}
					
					if(livello>=1) {
						if(parziale.get(livello-1).compareTo(parziale.get(livello))!=0) {
							tot=tot-COST;
						}
					}
					
					parziale.remove(parziale.size()-1);
				}
				
			} // questa è la parentesi che chiude il for
			
			
		}
		
	}

	private boolean controlloGiorniMaxCitta(List<String> parziale) {
		
		int cntMilano=0;
		int cntTorino=0;
		int cntGenova=0;
		
		boolean ok=true;
		
		for(int i=0;i<parziale.size();i++) {
			if(parziale.get(i).compareTo("Milano")==0) {
				cntMilano++;
			}
			if(parziale.get(i).compareTo("Torino")==0) {
				cntTorino++;
			}
			if(parziale.get(i).compareTo("Genova")==0) {
				cntGenova++;
				
			}
		}
			
			
		if(cntMilano<=NUMERO_GIORNI_CITTA_MAX && cntTorino<=NUMERO_GIORNI_CITTA_MAX && cntGenova<=NUMERO_GIORNI_CITTA_MAX) {
			ok = true;
		} else {
			ok = false;
		}
		
		/*if(cntMilano>0 && cntTorino>0 && cntGenova>0) {
			ok = true;
		} else {
			ok = false;
		}*/
		
		return ok;
	}

	private boolean controllogiorniMinCitta(List<String> parziale) {
		
		boolean ok = true;
		
		if(parziale.get(0).compareTo(parziale.get(1))==0 && parziale.get(1).compareTo(parziale.get(2))==0){
			//questo è giusto
		} else {
			ok=false;
		}
		
		for(int i=0;i<parziale.size()-3;i++) {
			if(i>=1) {
				if(parziale.get(i-1).compareTo(parziale.get(i))!=0) {
					for(int j=i;j<i+3;j++) {
						if(parziale.get(j).compareTo(parziale.get(i))!=0) {
							ok=false;
						}
					}
			}
			}	
		
		}
		
		if(parziale.get(14).compareTo(parziale.get(13))==0 && parziale.get(13).compareTo(parziale.get(12))==0){
			
		}	else {
			ok=false;
		}
		
		return ok;
	}
	
	
	/*public boolean possoAggiungere(List<String> parziale, String citta) {
		
		if(parziale.size()==1 && parziale.get(0).compareTo(citta)==0) {
			return true;
		}
		
		if(parziale.size()==2 && parziale.get(0).compareTo(citta)==0 && parziale.get(1).compareTo(citta)==0) {
			return true;
		}
		
		for(int i=2;i<parziale.size();i++) {
			if(parziale.get(i))
		}
		
	}*/

}
