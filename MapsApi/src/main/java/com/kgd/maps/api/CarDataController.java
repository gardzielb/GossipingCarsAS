package com.kgd.maps.api;

import com.kgd.maps.models.CarData;
import com.kgd.maps.repositories.CarDataRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/car_requests")
public class CarDataController {

    private final CarDataRepository carDataRepository;

    public CarDataController(@Autowired CarDataRepository carDataRepository) {
        this.carDataRepository = carDataRepository;
    }

    @DeleteMapping("")
    public void deleteById(@RequestParam("id") String Id) { carDataRepository.deleteById(new ObjectId(Id)); }

    @GetMapping("/all")
    public List<CarData> getAll() {
        return carDataRepository.findAll();
    }

    @PostMapping("/add")
    public CarData add(@RequestBody CarData carData) {
        return carDataRepository.insert(carData);
    }

    @PostMapping("/add_multiple")
    public List<CarData> addMultiple(@RequestBody List<CarData> datas) {
        return carDataRepository.insert(datas);
    }

    @DeleteMapping("/all")
    public void deleteAll() {
        carDataRepository.deleteAll();
    }
}
