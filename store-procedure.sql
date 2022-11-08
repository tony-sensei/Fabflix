use movieDB;
DELIMITER $$

create procedure add_star (in s_name varchar(100), in s_birthYear int, out starId varchar(10))
begin
	declare prev_id varchar(100);
    declare s_id varchar(100);
    select max(id) from stars into prev_id;
    -- lpad to keep leading zeros
    select concat('nm', lpad(substring(prev_id, 3, 7) + '1', 7, '0')) into s_id;
    -- -1 for not provided birth year --
	if (s_birthYear <=>  -1) then
		insert into stars (id, name) values (s_id, s_name);
	else 
		insert into stars (id, name, birthYear) values (s_id, s_name, s_birthYear);
	end if;
    set starId = s_id;
    select starId;
end
$$

create procedure add_movie (in m_title varchar(100), 
	in m_year int, in m_director varchar(100),
    in s_name varchar(100), in m_genre varchar(32))
begin
	declare prev_id varchar(100);
    declare m_id varchar(100);
    -- declare repeat_title varchar(100);
    declare movie_repeat int;
    declare s_id varchar(10);
    declare g_id int;
    declare repeat_star varchar(100);
    declare err_msg varchar(100);
    select max(id) from movies into prev_id;
    -- lpad to fill the leading zeros
    select concat('tt', lpad(substring(prev_id, 3, 7) + '1', 7, '0')) into m_id;
    -- check if movie already exists
    select count(if (year <=> m_year and director <=> m_director, 1, 0)) into movie_repeat
    from movies where title = m_title;
    -- check # of movie_repeat
    if (movie_repeat) then 
		select 'movie repeat error' into err_msg;
		select err_msg as answer;
	else 
		-- first insert movie into table
        insert into movies (id, title, year, director) values (m_id, m_title, m_year, m_director);
        -- check star and genre; we only need to link one of the stars with same names here
        select distinct s.name, s.id from stars as s where s.name = s_name limit 1 into repeat_star, s_id;
        -- if star is not repeated
        if (isnull(repeat_star)) then
			-- add star without birthYear
            call add_star(s_name, -1, @starId);
            -- link starId with movieId
			insert into stars_in_movies(starId, movieId) values (@starId, m_id);
		else 
			insert into stars_in_movies(starId, movieId) values (s_id, m_id);
        end if;
        
        select id from genres where name = m_genre into g_id;
        if (isnull(g_id)) then
			select max(id) + 1 from genres into g_id;
            insert into genres(id, name) values (g_id, m_genre);
            insert into genres_in_movies(genreId, movieId) values (g_id, m_id);
		else
			insert into genres_in_movies(genreId, movieId) values (g_id, m_id);
		end if;
		select concat(m_title, 'has been successfully added') as answer;
	end if;
end
$$

create procedure add_movie4 (inout m_id varchar(10), m_title varchar(100), 
	in m_year int, in m_director varchar(100)
    )
begin
	declare prev_id varchar(100);
    declare new_id varchar(10);
    declare movie_repeat int;
    declare err_msg varchar(100);
	if (isnull(m_id)) then
		select max(id) from movies into prev_id;
		-- lpad to fill the leading zeros
		select concat('tt', lpad(substring(prev_id, 3, 7) + '1', 7, '0')) into new_id;
        -- check if movie already exists
		select count(if (year <=> m_year and director <=> m_director, 1, 0)) into movie_repeat;
        if (movie_repeat) then 
			set m_id = '-1';
			select 'movie repeat error' into err_msg;
			select err_msg as answer;
		else 
			-- first insert movie into table
			insert into movies (id, title, year, director) values (new_id, m_title, m_year, m_director);
            set m_id = new_id;
            select 'success (id not provided)' into err_msg;
            select err_msg as answer;
		end if;
	else
		insert into movies (id, title, year, director) values (m_id, m_title, m_year, m_director);
        select 'success (id provided)' into err_msg;
		select err_msg as answer;
	end if;
end
$$

DELIMITER ;
