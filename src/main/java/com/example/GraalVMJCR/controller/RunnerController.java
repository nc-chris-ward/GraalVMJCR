package com.example.GraalVMJCR.controller;

import com.example.GraalVMJCR.model.CodeSubmission;
import com.example.GraalVMJCR.service.RunnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RunnerController {

    RunnerService runnerService;

    @Autowired
    public RunnerController(RunnerService runnerService) {
        this.runnerService = runnerService;
    }

    @PostMapping("/run")
    public ResponseEntity<String> compileAndRun(@RequestBody CodeSubmission codeSubmission) {
        String result = runnerService.compileAndRunCode(codeSubmission.code());
        return ResponseEntity.ok(result);
    }


}
