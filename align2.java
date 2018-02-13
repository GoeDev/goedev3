import java.io.*;
import java.math.*;
class align2{
	public static String seq1 = "A";
	public static String seq2 = "A";
	public static void main(String[] args){
		String sequences = args[0];
		String blosum = args[1];
		boolean global = Integer.parseInt(args[2]) == 1;
		boolean toggle = false;//String 1/String2 toggle
		int[][] matrix = LoadMatrix(blosum);
		//Einlesen der Datei und zerlegen in die Sequenzen
		File file = new File(sequences);
		if (!file.canRead() || !file.isFile())
			System.exit(0);
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader (file));
			String zeile = null;
			while ((zeile = input.readLine()) != null) {
				if (zeile.charAt (0) != '>')
					if(toggle)
						seq1 += zeile;
					else
						seq2 += zeile;
				else
					toggle = !toggle;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (input != null)
				input.close();
			} catch (IOException e) {
			}
		}
		System.out.println(seq1.substring(1));
		System.out.println(seq2.substring(1));
		align(1, matrix, global);
	}

	public static int[][] LoadMatrix(String blosum){ //Verarbeitet díe Datei BLOSUM62 zu einem passenden int[][]
		int[][] array = new int[20][20];
		int i = 0;
		int j = 0;
		int wert = 0;
		String s = "GPAVLIMCFYWHKRQNEDST";	//"Indexstring" für Matrix G->0; P->1 etc
		File file = new File(blosum);
		if (!file.canRead() || !file.isFile())
			System.exit(0);
		BufferedReader input = null;
		try {
			input = new BufferedReader(new FileReader (file));
			String zeile = null;
			while ((zeile = input.readLine()) != null) {//Zerlegt die Zeile in char, char, int
				i = s.indexOf(zeile.charAt(1));
				j = s.indexOf(zeile.charAt(5));
				array[i][j] = Integer.parseInt(zeile.substring(7).replaceAll("\\s+", ""));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (input != null)
				input.close();
			} catch (IOException e) {
			}
		}
		return array;
	}

	public static int getWeight(int[][] matrix, int i, int j){
		String t = "GPAVLIMCFYWHKRQNEDST";
		return(matrix[t.indexOf(seq1.charAt(i))][t.indexOf(seq2.charAt(j))]);
	}

	public static boolean CheckChars(String s){//checkt nen String auf die 20 basen-chars
		String t = "GPAVLIMCFYWHKRQNEDST";
		for (int i = 0; i < s.length(); i++)
			if (s.indexOf(s.charAt(i)) == -1)
				return false;
		return true;
	}

	public static int Maximum(int i, int j, int k, boolean global){//Gibt den größten der 3 ints zurück
		if (i > j)
			if(i > k)
				return i;
		if (j > k)
				return j;
		if(!global && k<0) //lokales alignment
			return 0;
		return k;

	}

	public static void align(int g, int[][] matrix, boolean global){
		Node[][] array = new Node[seq1.length()][seq2.length()];
		int max;
		int path =3;
		for(int i = 0; i<seq1.length(); i++){
			for (int j = 0; j<seq2.length(); j++){
				if (i == 0 && j == 0){
					array[i][j] = new Node(0, 3);
				}
				if (i == 0 && j != 0){
					array[i][j] = new Node(array[i][j-1].getMax() - g, 0);
				}
				if(j == 0 && i != 0){
					array[i][j] = new Node(array[i-1][j].getMax() - g, 2);
				}
				if(i != 0 && j != 0){
					max = Maximum(array[i][j-1].getMax()-g, array[i-1][j-1].getMax() + getWeight(matrix, i, j), array[i-1][j].getMax()-g, global);//s fehlt
					if (max == array[i][j-1].getMax()-g)
						path = 0;
					if (max == array[i-1][j-1].getMax()+getWeight(matrix, i, j))
						path = 1;
					if (max == array[i-1][j].getMax()-g)
						path = 2;
					array[i][j] = new Node(max, path);
				}
			}
		}
		/*for(int i = 0; i<seq1.length(); i++){
			System.out.print(i + ":");
			for (int j = 0; j<seq2.length(); j++)
				System.out.print(array[i][j].getpath());
			System.out.println();	//gibt alle paths aus
		}*/
		int i = seq1.length()-1;
		int j = seq2.length()-1;
		boolean b = true;
		String s = "";
		while (b){
			path = array[i][j].getpath();
			//System.out.println("max" + array[i][j].getMax());
			switch(path){
				case 2: i--;
					break;
				case 1: j--;
						i--;
					break;
				case 0: j--;
					break;
			}
			if (path == 3)
				b = false;
			s = path + s;
		}
		output(s);/*
		System.out.print("Max:");
		for(int n =0; n < seq1.length(); n++){
			System.out.println();
			for(int m = 0; m < seq2.length(); m++){
				System.out.print(array[n][m].getMax() + " ");
			}
		}*/


	}
	public static void output (String path){
		path = path.substring(1);
		int i = 1;
		int j = 1;
		int k = 0;
		String eins = "";
		String zwei = "";
		while (k < path.length()){

			switch(path.charAt(k)){

				case '2':
					eins = eins + seq1.charAt(i);
					i++;
					zwei = zwei + "-";
					break;
				case '1':
					eins = eins + seq1.charAt(i);
					i++;
					zwei = zwei + seq2.charAt(j);
					j++;
					break;
				case '0':
					eins = eins + "-";
					zwei = zwei + seq2.charAt(j);
					j++;
					break;
			}
			k++;
		}
		if (i != seq1.length() && j != seq2.length()){

		}
		System.out.println();
		System.out.println(eins);
		System.out.println(zwei);

	}
}

class Node{
	public int max;
	public int path;

	public Node(){
		this.max = 0;
		this.path = 3;
	}
	public Node(int max, int path){
		this.max = max;
		this.path = path;
	}
	public int getpath(){
		return this.path;
	}
	public int getMax(){
		return this.max;
	}
}
