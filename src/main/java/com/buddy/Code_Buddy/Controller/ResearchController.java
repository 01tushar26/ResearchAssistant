package com.buddy.Code_Buddy.Controller;

import com.buddy.Code_Buddy.Clients.AiService;
import com.buddy.Code_Buddy.DTO.ResearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/research")
//this will allow it to access from the frontend
@CrossOrigin("*")
@RequiredArgsConstructor
public class ResearchController {
    private final AiService aiService;

    @PostMapping("/process")
    public ResponseEntity<String> generate(@RequestBody ResearchRequest researchRequest){
        String response = aiService.generate(researchRequest);
        return ResponseEntity.ok(response);
    }

}
