package me.partlysunny.util.classes;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RandomList<E> extends ArrayList<RandomList<E>.RandomCollectionObject<E>> {

    private final Random random;

    public RandomList() {
        this(new Random());
    }

    public RandomList(Collection<? extends RandomCollectionObject<E>> c) {
        this();
        addAll(c);
    }

    public RandomList(Random random) {
        this.random = random;
    }

    public Random getRandom() {
        return random;
    }

    public boolean add(E e, double chance) {
        return this.add(new RandomCollectionObject<E>(e, chance));
    }

    @Override
    public boolean remove(Object o) {
        return ((RandomList<Object>) this.clone()).stream().anyMatch((t) -> t.object.equals(o) && super.remove(t));
    }

    public double totalWeight() {
        return this.stream().mapToDouble(RandomCollectionObject::getWeight).sum();
    }

    public E raffle() {
        return raffle(this);
    }

    public E raffle(Predicate<RandomCollectionObject<E>> predicate) {
        RandomList<E> aux = this.stream()
                .filter(predicate)
                .collect(Collectors.toCollection(RandomList::new));

        return raffle(aux);
    }

    private E raffle(RandomList<E> list) {
        NavigableMap<Double, RandomCollectionObject<E>> auxMap = new TreeMap<>();

        list.forEach((rco) -> {
            double auxWeight = auxMap.values().stream().mapToDouble(RandomCollectionObject::getWeight).sum();
            auxWeight += rco.getWeight();

            auxMap.put(auxWeight, rco);
        });

        double totalWeight = list.getRandom().nextDouble() * auxMap.values().stream().mapToDouble(RandomCollectionObject::getWeight).sum();

        return auxMap.ceilingEntry(totalWeight).getValue().getObject();
    }

    public class RandomCollectionObject<T> {

        private final T object;
        private final Double weight;

        private RandomCollectionObject(T e, Double weight) {
            this.object = e;
            this.weight = weight;
        }

        public T getObject() {
            return this.object;
        }

        public Double getWeight() {
            return this.weight;
        }
    }
}
