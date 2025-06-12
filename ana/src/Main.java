//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        }
    public String solution(String S, int K) {
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        int index = 0;

        // Encontrar o índice correspondente ao dia S
        for (int i = 0; i < days.length; i++) {
            if (days[i].equals(S)) {
                index = i;
                break;
            }
        }

        // Calcular o novo índice após K dias
        int newIndex = (index + K) % 7;

        return days[newIndex];
    }

    public String solution2(String S, int K) {
        int index = 0;
        String dias[] = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        for(int i = 0; i < dias.length; i++){

        }
    }
    }
