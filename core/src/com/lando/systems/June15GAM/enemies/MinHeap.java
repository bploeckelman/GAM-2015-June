package com.lando.systems.June15GAM.enemies;


public class MinHeap<T>
{
    private int count;
    private int capacity;
    private BreadCrumb temp;
    private BreadCrumb mheap;
    private BreadCrumb[] array;
    private BreadCrumb[] tempArray;

    public int count ()
    {
        return this.count;
    }

    public MinHeap() {
        this(16);
    }

    public MinHeap(int capacity)
    {
        this.count = 0;
        this.capacity = capacity;
        array = new BreadCrumb[capacity];
    }

    public void buildHead()
    {
        int position;
        for (position = (this.count - 1) >> 1; position >= 0; position--)
        {
            this.minHeapify(position);
        }
    }

    public Boolean contains(BreadCrumb item)
    {
        for (int i = 0; i < count; i++)
        {
            if (array[i].equals(item)) return true;
        }
        return false;
    }

    public BreadCrumb find(BreadCrumb item)
    {
        for (int i =0; i < count; i++)
        {
            if (array[i].equals(item)) return array[i];
        }

        return null;
    }

    public void add(BreadCrumb item)
    {
        this.count++;
        if (this.count > this.capacity)
        {
            doubleArray();
        }
        this.array[this.count - 1] = item;
        int position = this.count - 1;

        int parentPosition = ((position - 1) >> 1);

        while (position > 0 && array[parentPosition].compareTo(array[position]) > 0)
        {
            temp = this.array[position];
            this.array[position] = this.array[parentPosition];
            this.array[parentPosition] = temp;
            position = parentPosition;
            parentPosition = ((position - 1) >> 1);
        }
    }

    private void doubleArray()
    {
        this.capacity <<= 1;
        tempArray = new BreadCrumb[this.capacity];
        copyArray(this.array, tempArray);
        this.array = tempArray;
    }

    private static void copyArray(BreadCrumb[] source, BreadCrumb[] destination)
    {
        int index;
        for (index = 0; index < source.length; index++)
        {
            destination[index] = source[index];
        }
    }

    public BreadCrumb peek() throws Exception
    {
        if (this.count == 0)
        {
            throw new Exception("Heap is empty");
        }
        return this.array[0];
    }


    public BreadCrumb extractFirst()
    {
        temp = this.array[0];
        this.array[0] = this.array[this.count - 1];
        this.count--;
        this.minHeapify(0);
        return temp;
    }

    private void minHeapify(int position)
    {
        do
        {
            int left = ((position << 1) + 1);
            int right = left + 1;
            int minPosition;

            if (left < count && array[left].compareTo(array[position]) < 0)
            {
                minPosition = left;
            }
            else
            {
                minPosition = position;
            }

            if (right < count && array[right].compareTo(array[minPosition]) < 0)
            {
                minPosition = right;
            }

            if (minPosition != position)
            {
                mheap = this.array[position];
                this.array[position] = this.array[minPosition];
                this.array[minPosition] = mheap;
                position = minPosition;
            }
            else
            {
                return;
            }

        } while (true);
    }
}