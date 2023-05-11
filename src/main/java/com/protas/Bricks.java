package com.protas;

import com.protas.util.InstructionReader;

import java.util.*;
import java.util.stream.Collectors;

public class Bricks {

    public static void main(String[] args) {

        String filePath = null;
        if(args.length > 0) {
            filePath = args[0];
        }

        if(filePath != null) {

            // reading instructions into List<String>
            List<String> instructions = InstructionReader.read(filePath);

            if(instructionsCorrect(instructions)) {
                // mapping the instructions into Map<Integer, List<String> Integer <--> List<String pairs
                Map<Integer, List<String>> mappedInstructions = mapInstructions(instructions);

                // building the city using blocks and instructions from Map
                build(mappedInstructions);
            }
            else {
                System.out.println("klops");
            }

        }
        else {
            System.out.println("Incorrect input path");
        }

    }

    private static boolean instructionsCorrect(List<String> instructions) {
        return instructions.stream()
                .allMatch(c -> Character.isDigit(c.charAt(0)) &&
                c.substring(2).chars().allMatch(character -> character >= 'A' && character <= 'O') &&
                c.length() == 6);
    }

    private static void build(Map<Integer, List<String>> instructions) {

        int[] results = new int[]
                {0, 0, 0, 0, 0, 0};

        // box with bricks
        List<String> bricksBox = instructions.entrySet()
                .stream()
                .filter(entry -> entry.getKey() == 0)
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.toList());

        // sorted -> first divisible by 3 and without '0' key
        Map<Integer, List<String>> sortedInstructions = sortMapDivisibleBy3(
                instructions.entrySet()
                        .stream()
                        .filter(entry -> entry.getKey() != 0)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );

        // looping for each entry of sortedInstructions Map
        for(Map.Entry<Integer, List<String>> instruction : sortedInstructions.entrySet()) {

            // receiving the list of bricks from current Integer key
            List<String> bricks = instruction.getValue();

            if (checkAllElementsInBox(bricks, bricksBox)) {
                for (String brick : bricks) {
                    bricksBox.remove(brick);
                }

                if(instruction.getKey() % 3 == 0) results[0]++;
                else results[1]++;

                // incrementing the built constructions counter
                results[4]++;
            }
            else {

                // for each brick in bricks list
                for(int i = 0; i < bricks.size(); i++) {

                    // incrementing the missing blocks counter
                    if(!bricksBox.contains(bricks.get(i))) {
                        results[3]++;
                    }
                    if(bricksBox.contains(bricks.get(i)) && !bricks.get(i).equals(bricksBox.get(i))) {
                        results[3]++;
                    }
                }

                // incrementing the counter of constructions that could not have been built
                results[5]++;
            }

        }

        // setting the counter of bricks left in the box
        results[2] += bricksBox.size();

        // souting the result of array
        Arrays.stream(results)
                .forEach(x -> System.out.println(x));
    }

    // method to map instructions from List<String> to key-value pair Map<Integer, List<String>
    private static Map<Integer, List<String>> mapInstructions(List<String> instructions) {
        Map<Integer, List<String>> resultMap = new HashMap<>();

        // for every instruction in instructions list mapping the parsed values into key-value pair Map
        for(String instruction : instructions) {
            int key = Integer.parseInt(instruction.substring(0, 1));
            String value = instruction.substring(2);

            // if entry already has key just adds the element to the existing list
            if (resultMap.containsKey(key)) {
                if(resultMap.get(key) != null) {
                    resultMap.get(key).add(value);
                }
                else {
                    resultMap.put(key, new ArrayList<>());
                }
            }
            // otherwise -> it creates a new list and put it into pair
            else {
                List<String> list = new ArrayList<>();
                list.add(value);
                resultMap.put(key, list);
            }
        }

        return resultMap;
    }

    // method to check whether all elements in box are existing in bricks and so on...
    private static boolean checkAllElementsInBox(List<String> bricks, List<String> box) {
        List<String> tempList = new ArrayList<>(box);
        tempList.retainAll(bricks);
        return tempList.size() == bricks.size();
    }

    // sorting the Map in order -> first come these where key is divisible by 3 and the rest is after these keys
    private static Map<Integer, List<String>> sortMapDivisibleBy3(Map<Integer, List<String>> unsortedMap) {

        // using LinkedHashMap because it remembers the order of putting values
        Map<Integer, List<String>> sortedHashMap = new LinkedHashMap<>();

        // putting into output Map these values that are divisible by 3
        unsortedMap.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .forEach(entry -> {
                    if (entry.getKey() % 3 == 0) {
                        sortedHashMap.put(entry.getKey(), entry.getValue());
                    }
                });

        // putting into output Map these values that aren't divisible by 3
        unsortedMap.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey)) //
                .forEach(entry -> {
                    if (entry.getKey() % 3 != 0) {
                        sortedHashMap.put(entry.getKey(), entry.getValue());
                    }
                });

        return sortedHashMap;
    }

}
