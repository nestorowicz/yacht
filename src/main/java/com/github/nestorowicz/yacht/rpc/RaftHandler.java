package com.github.nestorowicz.yacht.rpc;

import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.http.HttpServlet;

public abstract class RaftHandler extends HttpServlet {

    protected abstract ServletHolder getServlet();

    protected abstract String getPath();
}
