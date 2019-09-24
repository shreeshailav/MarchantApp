package com.hashedin.marchantapp.Services.models;

public class Transaction {

    private String breed;

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

//    public void fetchImages(Callback<DogBreedImages> callback) {
//        Api.getApi().getImagesByBreed(this.breed).enqueue(callback);
//    }

}
