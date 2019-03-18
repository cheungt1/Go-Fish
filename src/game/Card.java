package game;

public class Card {

    public enum Suit {
        DIAMOND,
        CLUB,
        HEART,
        SPADE,
        NONE,
        ;

        public static Suit get(String suit) {
            suit = suit.toLowerCase();
            switch (suit) {
                case "diamond":
                    return DIAMOND;
                case "club":
                    return CLUB;
                case "heart":
                    return HEART;
                case "spade":
                    return SPADE;
                default:
                    return NONE;
            }
        }

        @Override
        public String toString() {
            switch (this) {
                case DIAMOND:
                    return "DIAMOND";
                case CLUB:
                    return "CLUB";
                case HEART:
                    return "HEART";
                case SPADE:
                    return "SPADE";
                default:
                    return "NONE";
            }
        }
    }

    public static final String[] CARD_NAMES = {
            "Joker", "A", "2", "3", "4", "5", "6",
            "7", "8", "9", "10", "J", "Q", "K"
    };

    private int rank;
    private Suit suit;

    public Card(int rank) {
        this(rank, Suit.NONE);
    }

    public Card(int rank, String suit) {
        this(rank, Suit.get(suit));
    }

    public Card(int rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public String getRank() {
        return CARD_NAMES[rank];
    }

    public void setRank(String rank) {
        setRank(rankOf(rank));
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public void setSuit(String suit) {
        setSuit(Suit.get(suit));
    }

    public void setSuit(Suit suit) {
        this.suit = suit;
    }

    public static int rankOf(String rank) {
        for (int i = 0; i < CARD_NAMES.length; i++) {
            if (rank.equalsIgnoreCase(CARD_NAMES[i]))
                return i;
        }

        throw new IllegalArgumentException("Illegal rank: " + rank);
    }

    @Override
    public String toString() {
        return CARD_NAMES[rank] + " of " + suit.toString() + "S";
    }
}
