package com.example.secondchance

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProductDao {

    @Insert
    fun insertProduct(products: List<Product>)


    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    fun addProduct(product: Product)
    @Delete
    fun deleteProduct(vararg  product: Product)
    @Update
    fun updateProduct(product: Product)

    @Query("SELECT * FROM ProductsTable ORDER BY `Name` ASC")
    fun getProduct() : LiveData<List<Product>>

    @Query("SELECT * FROM ProductsTable WHERE id LIKE :id ")
    fun getProduct(id:Int) : LiveData<Product?>

}