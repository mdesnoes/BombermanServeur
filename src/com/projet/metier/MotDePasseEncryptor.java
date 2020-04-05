package com.projet.metier;

import org.jasypt.util.text.BasicTextEncryptor;

/* singleton */
public class MotDePasseEncryptor {
	
	private static MotDePasseEncryptor uniqueInstance = null;
	private static final String KEY = "keyMDPEncryptor";
	
    private BasicTextEncryptor textEncryptor;

    
    private MotDePasseEncryptor() {
    	this.textEncryptor = new BasicTextEncryptor();
        this.textEncryptor.setPassword(KEY); // Clé secrete

    }
    
    public static MotDePasseEncryptor getInstance() {
    	if(uniqueInstance == null) {
			uniqueInstance = new MotDePasseEncryptor();
		}
		return uniqueInstance;
    }
    
    public String crypter(String mdp) {
        return this.textEncryptor.encrypt(mdp);
    }
    
    public String decrypter(String mdpChiffre) {
    	return this.textEncryptor.decrypt(mdpChiffre);
    }
    
}
