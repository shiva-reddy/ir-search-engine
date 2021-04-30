/*
    @author: Shiva Reddy Kokilathota Jagirdar
*/
package indexing;

import utilities.Porter;

public class PorterStemmer implements StemmingStrategy{
    final Porter porter;

    PorterStemmer(){
        this.porter = new Porter();
    }

    @Override
    public String stemToken(String token) {
        return porter.stripAffixes(token);
    }
}
