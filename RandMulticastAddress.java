

import java.util.Random;

public class RandMulticastAddress {
	/**
	 * classe contenente un metodo per la creazione di un indirizzo ip
	 * multicast con ambito globale, in modo che il pacchetto possa viaggiare attraverso
	 * internt
	 * L'indirizzo ip generato sarà contenuto nel range da 224.0.1.0 a 238.255.255.255
	 * @return stringa contenente l'indirizzo ip generato
	 */
	public static final String getMulticastAddress() {
		Random rand = new Random();
		int a = rand.nextInt(15) + 224;
		int b = rand.nextInt(256);
		int c = rand.nextInt(256) + 1;
		int d = rand.nextInt(255);
		String address = a+"."+b+"."+c+"."+d;
		return address;
	}
}
