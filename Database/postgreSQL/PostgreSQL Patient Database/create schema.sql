-- Remove all (default) privileges from public role on public schema
REVOKE ALL ON SCHEMA public FROM public;

-- Allow postgres user to access public schema
GRANT USAGE ON SCHEMA public TO postgres;

-- Add all privileges back to postgres user on public schema
GRANT ALL ON ALL TABLES IN SCHEMA public TO postgres;

-- Create workspace schema
CREATE SCHEMA workspace;

-- Create restricted user
CREATE USER restricted
WITH PASSWORD '<password>';

-- Allow restricted user to access public schema
GRANT USAGE ON SCHEMA public TO restricted;

-- And workspace schema
GRANT USAGE ON SCHEMA workspace TO restricted;

-- Add select only for restricted user in public schema
GRANT SELECT ON ALL TABLES IN SCHEMA public TO restricted;

-- Add all for restricted user in workspace schema
GRANT ALL ON ALL TABLES IN SCHEMA workspace TO restricted;
