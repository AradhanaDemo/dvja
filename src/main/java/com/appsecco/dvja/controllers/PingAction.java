package com.appsecco.dvja.controllers;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PingAction extends BaseController {

    private String address;
    private String commandOutput;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCommandOutput() {
        return commandOutput;
    }

    public void setCommandOutput(String commandOutput) {
        this.commandOutput = commandOutput;
    }

    public String execute() {
        if(StringUtils.isEmpty(getAddress()))
            return INPUT;

        try {
            doExecCommand();
        } catch (Exception e) {
            addActionMessage("Error running command: " + e.getMessage());
        }

        return SUCCESS;
    }

    private void doExecCommand() throws IOException {
        Runtime runtime = Runtime.getRuntime();
        String[] command = { "/bin/bash", "-c", "ping -t 5 -c 5 " + getAddress() };
        Process process = runtime.exec(command);

        StringBuilder output = new StringBuilder("Output:\n\n");
        
        BufferedReader stdInputReader = null;
        BufferedReader stdErrorReader = null;
        
        try {
            stdInputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while((line = stdInputReader.readLine()) != null)
                output.append(line).append("\n");

            output.append("\n");
            output.append("Error:\n\n");

            stdErrorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while((line = stdErrorReader.readLine()) != null)
                output.append(line).append("\n");
        } finally {
            if(stdInputReader != null) {
                try {
                    stdInputReader.close();
                } catch(IOException e) {
                    // Ignore
                }
            }
            if(stdErrorReader != null) {
                try {
                    stdErrorReader.close();
                } catch(IOException e) {
                    // Ignore
                }
            }
        }

        setCommandOutput(output.toString());
    }
}
