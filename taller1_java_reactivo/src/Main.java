import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Persona> personas = Arrays.asList(
                new Persona("Juan", 25, "Masculino"),
                new Persona("María", 30, "Femenino"),
                new Persona("Pedro", 40, "Masculino"),
                new Persona("Ana", 20, "Femenino")
        );

        //filter: Filtrar personas mayores de 25 años
        System.out.println(personas.stream()
                .filter(persona -> persona.getEdad() > 25)
                .collect(Collectors.toList()));

        //Map: Obtener lista con los nombres de las personas
        System.out.println(personas.stream()
                .map(Persona::getNombre)
                .collect(Collectors.toList()));

        //Reduce: Obtener la suma de las edades de las personas
        System.out.println(personas.stream()
                .map(Persona::getEdad)
                .reduce(0, Integer::sum));

        //Contar la cantidad de personas de cada género
        Map<String, Long> countByGenero = personas.stream()
                .collect(Collectors.groupingBy(Persona::getGenero, Collectors.counting()));

        System.out.println(countByGenero);

        //Calcular el promedio de edades de las personas
        System.out.println(personas.stream()
                .mapToInt(Persona::getEdad)
                .average()
                .orElseThrow());

        //Encontrar la persona con la edad más alta
        System.out.println(personas.stream()
                .max(Comparator.comparingInt(Persona::getEdad))
                .orElseThrow());


    }


}