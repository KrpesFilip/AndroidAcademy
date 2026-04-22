package com.example.androidacademyapi.data.repository

import com.example.androidacademyapi.data.network.RetrofitProductInstance
import com.example.androidacademyapi.data.model.Product
import com.example.androidacademyapi.data.model.ProductRequest

class RetrofitProductRepository : ProductRepository {
    override suspend fun getProducts(): Result<List<Product>> {
        return try {
            Result.success(RetrofitProductInstance.api.getProducts().products)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addProduct(request: ProductRequest): Result<Product> {
        return try {
            Result.success(
                RetrofitProductInstance.api.addProduct(request)
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProduct(
        id: Int,
        request: ProductRequest
    ): Result<Product> {
        return try {
            Result.success(RetrofitProductInstance.api.updateProduct(id,request))
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun deleteProduct(id: Int): Result<Product> {
        return try {
            Result.success(RetrofitProductInstance.api.deleteProduct(id))
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun getProduct(productId: Int): Result<Product> {
        return try {
            Result.success(
                RetrofitProductInstance.api.getProduct(productId)
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}